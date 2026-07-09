# Controle de Produção

App Android nativo (Kotlin + Jetpack Compose) para controle diário de produção de um
restaurante japonês, conforme especificação: login com dois perfis, lançamento de
produção da manhã/noite, fechamento do dia, histórico, dashboard, gestão de usuários
e exportação em PDF/Excel — tudo funcionando 100% offline com Room.

## Como gerar o APK sem instalar nada (GitHub Actions)

O projeto já vem com um workflow pronto em `.github/workflows/build-apk.yml` que
compila o APK automaticamente na nuvem. Passo a passo:

1. Crie uma conta gratuita em [github.com](https://github.com) (se ainda não tiver).
2. Crie um repositório novo (pode ser privado), por exemplo `controle-producao`.
3. Suba todo o conteúdo desta pasta `ControleProducao` para esse repositório.
   Formas mais fáceis, sem usar linha de comando:
   - No GitHub, clique em **"uploading an existing file"** na página do repositório
     recém-criado e arraste todos os arquivos e pastas, **ou**
   - Se preferir linha de comando:
     ```
     cd ControleProducao
     git init
     git add .
     git commit -m "Primeira versão do app"
     git branch -M main
     git remote add origin https://github.com/SEU_USUARIO/controle-producao.git
     git push -u origin main
     ```
4. Assim que o push terminar, vá na aba **Actions** do repositório no GitHub.
   O workflow "Build APK" já deve aparecer rodando automaticamente
   (leva uns 3–6 minutos na primeira vez).
5. Quando terminar (bolinha verde ✓), clique no build concluído e desça até
   **Artifacts** → baixe `ControleProducao-debug-apk` (é um .zip contendo o
   `app-debug.apk`).
6. Transfira o `app-debug.apk` para o celular (por WhatsApp, Google Drive, cabo USB
   etc.) e instale. Talvez seja preciso permitir "instalar de fontes desconhecidas"
   nas configurações do Android na primeira vez.

Esse é um **APK de debug** (não assinado para loja) — perfeito para testar no celular
do restaurante. Se um dia quiser publicar na Play Store, é preciso gerar uma versão
assinada (release), o que posso te ajudar a configurar depois.

## Como abrir o projeto no Android Studio (alternativa local)

1. Instale o **Android Studio** (versão Koala/2024.1 ou mais recente).
2. Abra a pasta `ControleProducao` inteira em *File → Open*.
3. Deixe o Gradle sincronizar (primeira vez pode demorar alguns minutos, baixando
   dependências do Compose, Room, Hilt e Navigation).
4. Rode em um emulador ou aparelho físico com Android 8.0 (API 26) ou superior.

## Primeiro acesso

Na primeira execução, o app cria automaticamente um usuário administrador:

- **Usuário:** `admin`
- **Senha:** `admin123`

Recomendo criar o(s) usuário(s) reais da equipe pela tela **Usuários** e, se quiser,
desativar ou trocar a senha do admin padrão depois.

## O que foi implementado

- **Login** com dois perfis (Administrador / Funcionário) e permissões diferentes,
  senha nunca armazenada em texto puro (hash SHA-256 + salt).
- **Tela inicial** com cards de atalho, mostrando o que já foi lançado no dia.
- **Produção da Manhã / Noite**: campos numéricos grandes com botões `+`/`-` e
  teclado numérico, para os 8 itens do cardápio pedidos. Confirmação antes de salvar.
- **Fechamento do dia**: lombo e barriga restantes (aceita decimais), observações
  opcionais, confirmação antes de finalizar.
- **Histórico** (admin): lista por data, com detalhe mostrando manhã, noite,
  fechamento, usuários responsáveis e horários.
- **Dashboard** (admin): totais do dia, produção por item, gráfico dos últimos 7 dias
  e do mês por semana (desenhado com Canvas nativo, sem lib externa), item mais
  produzido no mês.
- **Usuários** (admin): criar, listar e desativar usuários.
- **Exportação em PDF**: gerada com `android.graphics.pdf.PdfDocument` (nativo do
  Android, sem dependências externas).
- **Exportação em Excel (.xlsx)**: escrita manualmente como um pacote OOXML mínimo
  (ver nota abaixo — decisão importante de arquitetura).
- **Tema Material 3** claro/escuro (segue o sistema por padrão).
- **100% offline**: Room Database local, arquitetura pronta para acrescentar
  sincronização remota depois sem reescrever a UI (repositórios expõem `Flow`,
  bastaria adicionar uma fonte remota e mesclar).

## Nota importante sobre a exportação Excel

O pedido original mencionava Excel, e minha primeira ideia foi usar o **Apache POI**,
que é a biblioteca padrão em projetos Java/Kotlin para isso. Só que POI depende de
classes do pacote `java.awt`, que **não existem no Android (ART)** — o app compilaria
normalmente, mas quebraria com `NoClassDefFoundError` ao tentar exportar, em tempo de
execução. Por isso troquei por um gerador de `.xlsx` escrito à mão em
`util/ExcelExporter.kt`, que monta o pacote OOXML mínimo (zip com os XMLs de
workbook e planilha) sem depender de bibliotecas pesadas. O arquivo gerado abre
normalmente no Excel, Google Sheets e LibreOffice.

## Arquitetura

```
data/
  local/          Room: entities, DAOs, database, converters
  preferences/     DataStore: sessão do usuário e preferência de tema
  repository/      Camada entre UI e Room (Flow-based)
di/                Módulos Hilt (banco, preferências)
domain/model/      Enums do domínio (Turno, TipoUsuario, ItemProducao)
ui/
  theme/           Material 3 (cores, tipografia, claro/escuro)
  navigation/      NavGraph + rotas
  login/, home/, producao/, fechamento/, historico/, dashboard/, usuarios/
                   Cada tela em MVVM: Screen (Compose) + ViewModel (StateFlow)
  components/      Widgets reutilizáveis (campo numérico, card, diálogo de confirmação)
util/              PdfExporter, ExcelExporter, DateUtils, SecurityUtils
```

MVVM + Clean Architecture leve: os `ViewModel`s dependem só dos repositórios
(interfaces de dados), nunca do Room diretamente; as `Screen`s dependem só do
`ViewModel` correspondente via `hiltViewModel()`.

## Pontos que você provavelmente vai querer ajustar

- **Migrations do Room**: hoje está com `fallbackToDestructiveMigration()` (apaga o
  banco se o schema mudar). Bom para desenvolvimento; antes de publicar vale trocar
  por migrations reais para não perder dados de produção em atualizações do app.
- **Ícone do app**: coloquei um ícone placeholder simples (círculo branco sobre fundo
  vermelho). Vale substituir pela marca do restaurante.
- **Edição/exclusão de registros pelo admin**: o modelo de dados já suporta (telas de
  histórico mostram tudo), mas os botões de editar/excluir registro específico ainda
  não estão na tela de detalhe do histórico — hoje dá para reeditar reabrindo a tela
  de Produção/Fechamento do dia atual, mas não de dias passados. Se quiser, é uma
  extensão natural de `HistoricoDetalheScreen.kt`.
- **Sincronização remota**: a arquitetura (repositórios expondo `Flow`) já foi
  pensada para isso, mas não há nenhuma chamada de rede implementada ainda.
