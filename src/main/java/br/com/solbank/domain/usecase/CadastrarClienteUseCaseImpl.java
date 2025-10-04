package br.com.solbank.domain.usecase;

import br.com.solbank.domain.exception.BusinessException;
import br.com.solbank.domain.model.Cliente;
import br.com.solbank.ports.in.CadastrarClienteUseCase;
import br.com.solbank.ports.out.ClienteRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CadastrarClienteUseCaseImpl implements CadastrarClienteUseCase {
    private final ClienteRepository clienteRepository;

    public CadastrarClienteUseCaseImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public UUID executar(Comando cmd) {
        //Normaliza campos
        String nome = cmd.nome().trim();
        String cpfCnpjDigits = apenasDigitos(cmd.cpfCnpj());
        String email = cmd.email() == null ? null : cmd.email().trim();
        String telefone = cmd.telefone() == null ? null : apenasDigitos(cmd.telefone());

        // Regra simples: não permitir duplicidade de CPF/CNPJ
        Optional<Cliente> existente = clienteRepository.buscarPorCpfCnpj(cpfCnpjDigits);
        if (existente.isPresent()) {
            throw new BusinessException("CPF/CNPJ já cadastrado");
        }

        OffsetDateTime agora = OffsetDateTime.now();
        Cliente novo = new Cliente(
                UUID.randomUUID(),
                nome,
                cpfCnpjDigits,
                email,
                telefone,
                agora,
                agora
        );
        return clienteRepository.salvar(novo);
    }

    private String apenasDigitos(String s) {
        return s == null ? null : s.replaceAll("\\D", "");
    }

}