package com.careplus.map.service;

import com.careplus.map.exception.RecursoNaoEncontradoException;
import com.careplus.map.model.dto.*;
import com.careplus.map.model.entity.*;
import com.careplus.map.model.vo.EstatisticasVO;
import com.careplus.map.model.vo.RankingVO;
import com.careplus.map.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final MissaoCompletadaRepository missaoCompletadaRepository;
    private final UsuarioService usuarioService;
    private final MissaoService missaoService;

    @Transactional(readOnly = true)
    public AvatarResponseDTO buscarAvatar(Long usuarioId) {
        Avatar avatar = buscarAvatarPorUsuario(usuarioId);
        return toResponseDTO(avatar);
    }

    @Transactional
    public MissaoCompletadaResponseDTO completarMissao(Long usuarioId, Long missaoId, CompletarMissaoDTO dto) {
        Usuario usuario = usuarioService.buscarEntidade(usuarioId);
        Missao  missao  = missaoService.buscarEntidade(missaoId);

        if (!missao.getAtiva()) {
            throw new com.careplus.map.exception.RegraNegocioException(
                    "A missão '" + missao.getTitulo() + "' não está disponível no momento.");
        }

        Avatar avatar = buscarAvatarPorUsuario(usuarioId);

        // 1. Soma pontuação geral e os bônus das 5 categorias
        avatar.setPontosTotal(avatar.getPontosTotal() + missao.getPontosRecompensa());
        avatar.setSaude(avatar.getSaude() + missao.getBonusSaude());
        avatar.setHidratacao(avatar.getHidratacao() + missao.getBonusHidratacao());
        avatar.setSono(avatar.getSono() + missao.getBonusSono());
        avatar.setExercicio(avatar.getExercicio() + missao.getBonusExercicio());
        avatar.setBemEstar(avatar.getBemEstar() + missao.getBonusBemEstar());

        // 2. Regra de Evolução: Se as 5 baterem 100, sobe de nível e reseta
        if (avatar.getSaude() >= 100 && avatar.getHidratacao() >= 100 &&
                avatar.getSono() >= 100 && avatar.getExercicio() >= 100 &&
                avatar.getBemEstar() >= 100) {

            avatar.setNivel(avatar.getNivel() + 1); // Level UP!
            avatar.setSaude(0);
            avatar.setHidratacao(0);
            avatar.setSono(0);
            avatar.setExercicio(0);
            avatar.setBemEstar(0);
        } else {
            // Se não subiu de nível, garante que as barras não passem de 100 visualmente
            avatar.setSaude(Math.min(100, avatar.getSaude()));
            avatar.setHidratacao(Math.min(100, avatar.getHidratacao()));
            avatar.setSono(Math.min(100, avatar.getSono()));
            avatar.setExercicio(Math.min(100, avatar.getExercicio()));
            avatar.setBemEstar(Math.min(100, avatar.getBemEstar()));
        }

        avatarRepository.save(avatar);

        MissaoCompletada registro = MissaoCompletada.builder()
                .usuario(usuario)
                .missao(missao)
                .observacao(dto != null ? dto.getObservacao() : null)
                .build();
        MissaoCompletada salvo = missaoCompletadaRepository.save(registro);

        return MissaoCompletadaResponseDTO.builder()
                .id(salvo.getId())
                .usuarioId(usuarioId)
                .missaoId(missaoId)
                .tituloMissao(missao.getTitulo())
                .observacao(salvo.getObservacao())
                .completadaEm(salvo.getCompletadaEm())
                .avatarAtualizado(toResponseDTO(avatar))
                .build();
    }

    @Transactional(readOnly = true)
    public EstatisticasVO buscarEstatisticas(Long usuarioId) {
        usuarioService.buscarEntidade(usuarioId);
        Avatar avatar = buscarAvatarPorUsuario(usuarioId);

        long totalMissoes = missaoCompletadaRepository.countByUsuarioId(usuarioId);

        List<Object[]> raw = missaoCompletadaRepository.countMissoesPorCategoria(usuarioId);
        Map<String, Long> porCategoria = new LinkedHashMap<>();
        for (Object[] row : raw) {
            porCategoria.put(row[0].toString(), (Long) row[1]);
        }

        int pontosProxNivel = (avatar.getNivel() + 1) * 100;
        int pontosParaSubir = Math.max(0, pontosProxNivel - avatar.getPontosTotal());

        return EstatisticasVO.builder()
                .totalMissoesCompletadas(totalMissoes)
                .missoesPorCategoria(porCategoria)
                .pontosTotal(avatar.getPontosTotal())
                .nivelAtual(avatar.getNivel())
                .pontosParaProximoNivel(pontosParaSubir)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RankingVO> buscarRanking() {
        List<Avatar> avatares = avatarRepository.findRankingGlobal();
        AtomicInteger posicao = new AtomicInteger(1);

        return avatares.stream().map(a -> {
            long missoes = missaoCompletadaRepository.countByUsuarioId(a.getUsuario().getId());
            return RankingVO.builder()
                    .posicao(posicao.getAndIncrement())
                    .usuarioId(a.getUsuario().getId())
                    .nomeUsuario(a.getUsuario().getNome())
                    .nomeAvatar(a.getNomeAvatar())
                    .nivel(a.getNivel())
                    .pontosTotal(a.getPontosTotal())
                    .totalMissoesCompletadas(missoes)
                    .build();
        }).collect(Collectors.toList());
    }

    private Avatar buscarAvatarPorUsuario(Long usuarioId) {
        return avatarRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Avatar para o usuário com ID " + usuarioId + " não encontrado."));
    }

    public AvatarResponseDTO toResponseDTO(Avatar a) {
        return AvatarResponseDTO.builder()
                .id(a.getId())
                .usuarioId(a.getUsuario().getId())
                .nomeAvatar(a.getNomeAvatar())
                .nivel(a.getNivel())
                .pontosTotal(a.getPontosTotal())
                .saude(a.getSaude())
                .hidratacao(a.getHidratacao())
                .sono(a.getSono())
                .exercicio(a.getExercicio())
                .bemEstar(a.getBemEstar())
                .atualizadoEm(a.getAtualizadoEm())
                .build();
    }
}