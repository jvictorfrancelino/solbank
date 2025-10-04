package br.com.solbank.adapters.out.jdbc;

import br.com.solbank.domain.model.Cliente;
import br.com.solbank.ports.out.ClienteRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClienteRepositoryImpl implements ClienteRepository {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate named;

    public ClienteRepositoryImpl(JdbcTemplate jdbc, NamedParameterJdbcTemplate named) {
        this.jdbc = jdbc;
        this.named = named;
    }

    private static final RowMapper<Cliente> MAPPER = (rs, rowNum) -> new Cliente(
            UUID.fromString(rs.getString("id")),
            rs.getString("nome"),
            rs.getString("cpf_cnpj"),
            rs.getString("email"),
            rs.getString("telefone"),
            rs.getObject("criado_em", OffsetDateTime.class),
            rs.getObject("atualizado_em", OffsetDateTime.class)
    );

    @Override
    public UUID salvar(Cliente c) {
        UUID id = Optional.ofNullable(c.id()).orElse(UUID.randomUUID());
        // Pode deixar o banco preencher criado_em/atualizado_em (DEFAULT NOW())
        jdbc.update("""
                INSERT INTO clientes(id, nome, cpf_cnpj, email, telefone)
                VALUES (?, ?, ?, ?, ?)
                """, id, c.nome(), c.cpfCnpj(), c.email(), c.telefone());
        return id;
    }

    @Override
    public Optional<Cliente> buscarPorCpfCnpj(String cpfCnpjDigits) {
        return jdbc.query("""
                SELECT id, nome, cpf_cnpj, email, telefone, criado_em, atualizado_em
                FROM clientes
                WHERE cpf_cnpj = ? 
                """, MAPPER, cpfCnpjDigits
        ).stream().findFirst();
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jdbc.query("""
                SELECT id, nome, cpf_cnpj, email, telefone, criado_em, atualizado_em
                FROM clientes
                WHERE id=? 
                """, MAPPER, id
                ).stream().findFirst();
    }

    @Override
    public List<Cliente> pesquisar (UUID id,
                                    String nomeLike,
                                    String cpfCnpjDigits,
                                    String emailLike,
                                    String telefoneDigits,
                                    int limit, int offset){
        //TODO o que é esse WHERE 1-1?
        StringBuilder sql = new StringBuilder("""
                SELECT id, nome, cpf_cnpj, email, telefone, criado_em, atualizado_em
                FROM clientes
                WHERE 1=1
                """);
        MapSqlParameterSource p = new MapSqlParameterSource();

        if(id != null){
            sql.append("AND id = :id");
            p.addValue("id", id);
        }

        if (nomeLike != null){
            sql.append(" AND nome ILIKE :nome");
            p.addValue("nome", nomeLike);
        }

        if (cpfCnpjDigits != null){
            sql.append(" AND cpf_cnpj = :cpf");
            p.addValue("cpf", cpfCnpjDigits);
        }

        if (emailLike != null){
            sql.append(" AND cpf_cnpj = :email");
            p.addValue("email", emailLike);
        }

        if (telefoneDigits != null) {
            sql.append(" AND telefone = :tel");
            p.addValue("tel", telefoneDigits);
        }

        sql.append(" ORDER BY criado_em DESC LIMIT :limit OFFSET :offset");
        p.addValue("limit", limit);
        p.addValue("offset", offset);

        return named.query(sql.toString(), p, MAPPER);
    }

    @Override
    public int atualizarParcial(UUID id, String nome, String cpfCnpjDigits, String email, String telefone){
        StringBuilder set = new StringBuilder("atualizado_em = NOW()");
        //TODO perguntar ao gpt o que está acontecendo abaixo
        MapSqlParameterSource p = new MapSqlParameterSource().addValue("id", id);

        if (nome != null) {
            set.append(", nome = :nome");
            p.addValue("nome", nome);
        }

        if (cpfCnpjDigits != null) {
            set.append(", cpf_cnpj = :cpf");
            p.addValue("cpf", cpfCnpjDigits);
        }

        if (email != null) {
            set.append(", email = :email");
            p.addValue("email", email);
        }

        if(telefone != null) {
            set.append(", telefone = :tel");
            p.addValue("tel", telefone);
        }

        // se nada além do timestamp mudar, ainda consideramos update válido
        String sql = "UPDATE clientes SET " + set + " WHERE id = :id";
        return named.update(sql, p);
    }

    @Override
    public int deletarPorId(UUID id){
        return jdbc.update("DELETE FROM clientes WHERE id = ", id);
    }
}