package com.careplus.map.controller;

import com.careplus.map.model.dto.*;
import com.careplus.map.model.vo.EstatisticasVO;
import com.careplus.map.model.vo.RankingVO;
import com.careplus.map.service.AvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Avatar & Gamificação", description = "Consulta de avatar, conclusão de missões, estatísticas e ranking")
public class AvatarController {

    private final AvatarService avatarService;

    // avatar

    @GetMapping("/usuarios/{usuarioId}/avatar")
    @Operation(summary = "Consultar avatar", description = "Retorna o estado atual do avatar do usuário")
    public ResponseEntity<AvatarResponseDTO> buscarAvatar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(avatarService.buscarAvatar(usuarioId));
    }

    // missões

    @PostMapping("/usuarios/{usuarioId}/missoes/{missaoId}/completar")
    @Operation(
        summary = "Completar missão",
        description = "Registra a conclusão de uma missão pelo usuário e aplica os bônus ao avatar"
    )
    public ResponseEntity<MissaoCompletadaResponseDTO> completarMissao(
            @PathVariable Long usuarioId,
            @PathVariable Long missaoId,
            @Valid @RequestBody(required = false) CompletarMissaoDTO dto) {
        return ResponseEntity.ok(avatarService.completarMissao(usuarioId, missaoId, dto));
    }

    // estatisticas

    @GetMapping("/usuarios/{usuarioId}/estatisticas")
    @Operation(
        summary = "Estatísticas do usuário",
        description = "Retorna o progresso do usuário: missões completadas, distribuição por categoria e nível"
    )
    public ResponseEntity<EstatisticasVO> buscarEstatisticas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(avatarService.buscarEstatisticas(usuarioId));
    }

    // ranking

    @GetMapping("/ranking")
    @Operation(summary = "Ranking global", description = "Lista todos os usuários ordenados por pontuação (decrescente)")
    public ResponseEntity<List<RankingVO>> buscarRanking() {
        return ResponseEntity.ok(avatarService.buscarRanking());
    }
}
