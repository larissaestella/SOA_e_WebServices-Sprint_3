package com.careplus.map.model.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletarMissaoDTO {
    @Size(max = 100, message = "A observação pode ter no máximo 100 caracteres")
    private String observacao;
}