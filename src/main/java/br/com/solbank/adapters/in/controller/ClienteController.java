package br.com.solbank.adapters.in.controller;

import br.com.solbank.adapters.in.controller.dto.atualizarcliente.ClienteUpdateRequest;
import br.com.solbank.adapters.in.controller.dto.buscarcliente.ClienteSearchResponse;
import br.com.solbank.adapters.in.controller.dto.cadastrarcliente.ClienteRequest;
import br.com.solbank.adapters.in.controller.dto.cadastrarcliente.ClienteResponse;
import br.com.solbank.common.logging.LogExec;
import br.com.solbank.domain.exception.NotFoundException;
import br.com.solbank.domain.model.Cliente;
import br.com.solbank.domain.model.ClienteFiltro;
import br.com.solbank.ports.in.AtualizarClienteUseCase;
import br.com.solbank.ports.in.BuscarClientesUseCase;
import br.com.solbank.ports.in.CadastrarClienteUseCase;
import br.com.solbank.ports.in.ExcluirClienteUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@Validated
public class ClienteController {

    private final CadastrarClienteUseCase cadastrarClienteUseCase;
    private final BuscarClientesUseCase buscarClientesUseCase;
    private final AtualizarClienteUseCase atualizarClienteUseCase;
    private final ExcluirClienteUseCase excluirClienteUseCase;

    public ClienteController(
            CadastrarClienteUseCase cadastrarClienteUseCase,
            BuscarClientesUseCase buscarClientesUseCase,
            AtualizarClienteUseCase atualizarClienteUseCase,
            ExcluirClienteUseCase excluirClienteUseCase
    ){
        this.cadastrarClienteUseCase = cadastrarClienteUseCase;
        this.buscarClientesUseCase = buscarClientesUseCase;
        this.atualizarClienteUseCase = atualizarClienteUseCase;
        this.excluirClienteUseCase = excluirClienteUseCase;
    }

    @PostMapping
    @LogExec("api-post-cliente")
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest req) {
        UUID id = cadastrarClienteUseCase.executar(
                new CadastrarClienteUseCase.Comando(
                        req.nome(), req.cpfCnpj(), req.email(), req.telefone()
                )
        );
        var resp = new ClienteResponse(id, req.nome(), somenteDigitos(req.cpfCnpj()),
                req.email(), req.telefone() == null ? null : somenteDigitos(req.telefone()));
        return ResponseEntity.created(URI.create("/clientes/" + id)).body(resp);
    }

    @GetMapping("/{id}")
    @LogExec("api-get-cliente-id")
    public ResponseEntity<ClienteSearchResponse> buscarPorId(@PathVariable UUID id){
        // Reaproveita o use case de busca com filtro id
        var lista = buscarClientesUseCase.executar(new ClienteFiltro(id, null, null, null, null), 1, 0);
        Cliente c = lista.stream().findFirst().orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + id));
        return ResponseEntity.ok(new ClienteSearchResponse(c.id(), c.nome(), c.cpfCnpj(), c.email(), c.telefone()));
    }

    @GetMapping
    @LogExec("api-get-clientes")
    public ResponseEntity<List<ClienteSearchResponse>> buscar(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false, name = "nome") String nome,
            @RequestParam(required = false, name = "cpf_cnpj") String cpfCnpj,
            @RequestParam(required = false, name = "email") String email,
            @RequestParam(required = false, name = "telefone") String telefone,
            @RequestParam(required = false, defaultValue = "50") int limit,
            @RequestParam(required = false, defaultValue = "0") int offset
    ) {
        var filtro = new ClienteFiltro(id, nome, cpfCnpj, email, telefone);
        var lista = buscarClientesUseCase.executar(filtro, limit, offset);
        var resp = lista.stream()
                .map(c -> new ClienteSearchResponse(c.id(), c.nome(), c.cpfCnpj(), c.email(), c.telefone()))
                .toList();
        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/{id}")
    @LogExec("api-patch-cliente")
    public ResponseEntity<ClienteResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ClienteUpdateRequest req
    ){
        atualizarClienteUseCase.executar(
                new AtualizarClienteUseCase.Comando(
                        id,
                        req.nome(),
                        req.cpfCnpj(),
                        req.email(),
                        req.telefone()
                )
        );

        // retorna o recurso atualizado
        var lista = buscarClientesUseCase.executar(new ClienteFiltro(id, null, null, null, null), 1, 0);
        var c = lista.stream().findFirst().orElseThrow(() -> new NotFoundException("Cliente não encontrado: " + id));

        var resp = new ClienteResponse(c.id(), c.nome(), c.cpfCnpj(), c.email(), c.telefone());

        return ResponseEntity.ok(resp);

    }

    @DeleteMapping("/{id}")
    @LogExec("api-delete-cliente")
    public ResponseEntity<Void> excluir (@PathVariable UUID id){
        excluirClienteUseCase.executar(id);
        return ResponseEntity.noContent().build(); //204
    }

    private String somenteDigitos(String s) {
        return s == null ? null : s.replaceAll("\\D", "");
    }

}
