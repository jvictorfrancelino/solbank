package br.com.solbank.domain.usecase;

import br.com.solbank.common.logging.LogExec;
import br.com.solbank.domain.exception.BusinessException;
import br.com.solbank.domain.exception.NotFoundException;
import br.com.solbank.domain.model.Cliente;
import br.com.solbank.ports.in.AtualizarClienteUseCase;
import br.com.solbank.ports.out.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AtualizarClienteUseCaseImpl implements AtualizarClienteUseCase {

    private final ClienteRepository repo;

    public AtualizarClienteUseCaseImpl(ClienteRepository repo){
        this.repo = repo;
    }

    @Override
    @LogExec("atualizar-cliente")
    public void executar(Comando c){
        // 1) garantir que o cliente existe
        Cliente atual = repo.buscarPorId(c.id())
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + c.id()));

        // 2) normalizações e short-circuit (nada a atualizar)
        String nome         = isBlank(c.nome()) ? null : c.nome().trim();
        String cpfDigits    = digitsOrNull(c.cpfCnpj());
        String email        = isBlank(c.email()) ? null : c.email().trim();
        String telDigits    = digitsOrNull(c.telefone());

        // 3) regra de unicidade (se CPF/CNPJ alterado)
        if (cpfDigits != null && !cpfDigits.equals(atual.cpfCnpj())) {
            Optional<Cliente> outro = repo.buscarPorCpfCnpj(cpfDigits);
            if (outro.isPresent() && outro.get().id().equals(c.id())) {
                throw new BusinessException("CPF/CNPJ já cadastrado em outro cliente");
            }
        }

        // 4) atualizar parcialmente
        int updated = repo.atualizarParcial(c.id(), nome, cpfDigits, email, telDigits);
        if (updated == 0) {
            // concorrência/nenhuma linha alterada
            throw new NotFoundException("Cliente não encontrado" + c.id());
        }

    }

    private static boolean isBlank(String s){
        return s == null || s.trim().isEmpty();
    }

    private static String digitsOrNull(String s){
        if (s == null) return null;
        String d = s.replaceAll("\\D", "");
        return d.isEmpty() ? null : d;
    }

}
