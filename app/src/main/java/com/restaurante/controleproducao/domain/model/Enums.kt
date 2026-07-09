package com.restaurante.controleproducao.domain.model

/** Papel do usuário dentro do sistema. Controla permissões em toda a UI. */
enum class TipoUsuario {
    ADMINISTRADOR,
    FUNCIONARIO
}

/** Turno de produção do dia. */
enum class Turno {
    MANHA,
    NOITE
}

/** Cada item de cardápio controlado na produção diária. */
enum class ItemProducao(val label: String) {
    URAMAKI_SALMAO("Uramaki Salmão"),
    URAMAKI_SHIMEJI("Uramaki Shimeji"),
    URAMAKI_SKIN("Uramaki Skin"),
    URAMAKI_GRELHADO("Uramaki Grelhado"),
    NIGIRI_SALMAO("Nigiri Salmão"),
    NIGIRI_SKIN("Nigiri Skin"),
    JOW("Jow"),
    BATERA("Batera")
}
