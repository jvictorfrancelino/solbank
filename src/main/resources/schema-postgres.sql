-- =======================
-- Tipos (via DO $$ para compatibilidade ampla)
-- =======================
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipo_transacao') THEN
    CREATE TYPE tipo_transacao AS ENUM ('DEBITO', 'CREDITO', 'PIX', 'BOLETO');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'status_transacao') THEN
    CREATE TYPE status_transacao AS ENUM ('PENDENTE', 'CONFIRMADA', 'FALHOU');
  END IF;
END $$;
@@

-- =======================
-- Tabela: clientes
-- =======================
CREATE TABLE IF NOT EXISTS clientes (
  id            UUID PRIMARY KEY,
  nome          VARCHAR(120)   NOT NULL,
  cpf_cnpj      VARCHAR(14)    NOT NULL,
  email         VARCHAR(120),
  telefone      VARCHAR(20),
  criado_em     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
  CONSTRAINT uq_clientes_cpf_cnpj UNIQUE (cpf_cnpj),
  CONSTRAINT ck_clientes_cpf_cnpj_regex CHECK (
    cpf_cnpj ~ '^\d{11}$' OR cpf_cnpj ~ '^\d{14}$'
  )
);
@@

-- =======================
-- Tabela: carteiras
-- =======================
CREATE TABLE IF NOT EXISTS carteiras (
  id            UUID          PRIMARY KEY,
  cliente_id    UUID          NOT NULL REFERENCES clientes(id) ON DELETE CASCADE,
  saldo         NUMERIC(18,2) NOT NULL DEFAULT 0,
  moeda         CHAR(3)       NOT NULL DEFAULT 'BRL',
  criado_em     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
  atualizado_em TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);
@@

CREATE INDEX IF NOT EXISTS idx_carteiras_cliente ON carteiras(cliente_id);
@@

-- =======================
-- Tabela: transacoes
-- =======================
CREATE TABLE IF NOT EXISTS transacoes (
  id                 UUID             PRIMARY KEY,
  carteira_id        UUID             NOT NULL REFERENCES carteiras(id) ON DELETE RESTRICT,
  tipo               tipo_transacao   NOT NULL,
  status             status_transacao NOT NULL DEFAULT 'PENDENTE',
  valor              NUMERIC(18,2)    NOT NULL CHECK (valor > 0),
  descricao          TEXT,
  meta               JSONB,
  referencia_externa VARCHAR(100),
  criado_em          TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
  efetivada_em       TIMESTAMPTZ
);
@@

CREATE INDEX IF NOT EXISTS idx_transacoes_carteira  ON transacoes(carteira_id);
@@
CREATE INDEX IF NOT EXISTS idx_transacoes_criado_em ON transacoes(criado_em);
@@

CREATE INDEX IF NOT EXISTS idx_clientes_cpf ON clientes(cpf_cnpj);
CREATE INDEX IF NOT EXISTS idx_clientes_email ON clientes(email);
CREATE INDEX IF NOT EXISTS idx_clientes_tel ON clientes(telefone);
CREATE INDEX IF NOT EXISTS idx_clientes_nome_lower ON clientes((lower(nome)));