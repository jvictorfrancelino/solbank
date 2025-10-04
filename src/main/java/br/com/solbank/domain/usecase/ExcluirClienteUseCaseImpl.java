package br.com.solbank.domain.usecase;

import br.com.solbank.common.logging.LogExec;
import br.com.solbank.domain.exception.BusinessException;
import br.com.solbank.domain.exception.NotFoundException;
import br.com.solbank.ports.in.ExcluirClienteUseCase;
import br.com.solbank.ports.out.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExcluirClienteUseCaseImpl implements ExcluirClienteUseCase {

    private final ClienteRepository repo;

    public ExcluirClienteUseCaseImpl(ClienteRepository repo) {
        this.repo = repo;
    }

    @Override
    @LogExec("excluir-cliente")
    public void executar(UUID id){
        try{
            int rows = repo.deletarPorId(id);
            if (rows == 0){
                throw new NotFoundException("Cliente não encontrado"  + id);
            }
        } catch (DataIntegrityViolationException ex){
            throw new BusinessException("Não é possível excluir: Existem registros relacionados.");
        }
    }

}
