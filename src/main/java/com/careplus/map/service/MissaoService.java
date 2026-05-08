package com.careplus.map.service;

import com.careplus.map.exception.RecursoNaoEncontradoException;
import com.careplus.map.model.dto.MissaoCadastroDTO;
import com.careplus.map.model.dto.MissaoResponseDTO;
import com.careplus.map.model.entity.Missao;
import com.careplus.map.model.enums.CategoriaMissao;
import com.careplus.map.repository.MissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissaoService {

    private final MissaoRepository missaoRepository;
    @Transactional
    public MissaoResponseDTO criar(MissaoCadastroDTO dto) {
        Missao missao = Missao.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .categoria(dto.getCategoria())
                .pontosRecompensa(dto.getPontosRecompensa())
                .bonusSaude(dto.getBonusSaude() != null ? dto.getBonusSaude() : 0)
                .bonusHidratacao(dto.getBonusHidratacao() != null ? dto.getBonusHidratacao() : 0)
                .bonusSono(dto.getBonusSono() != null ? dto.getBonusSono() : 0)
                .bonusExercicio(dto.getBonusExercicio() != null ? dto.getBonusExercicio() : 0)
                .bonusBemEstar(dto.getBonusBemEstar() != null ? dto.getBonusBemEstar() : 0)
                .build();

        return toResponseDTO(missaoRepository.save(missao));
    }

    @Transactional(readOnly = true)
    public List<MissaoResponseDTO> listar(CategoriaMissao categoria) {
        List<Missao> missoes = (categoria != null)
                ? missaoRepository.findByAtivaAndCategoria(true, categoria)
                : missaoRepository.findByAtivaTrue();

        return missoes.stream().map(this::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public MissaoResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarEntidade(id));
    }

    @Transactional
    public MissaoResponseDTO atualizar(Long id, MissaoCadastroDTO dto) {
        Missao missao = buscarEntidade(id);

        missao.setTitulo(dto.getTitulo());
        missao.setDescricao(dto.getDescricao());
        missao.setCategoria(dto.getCategoria());
        missao.setPontosRecompensa(dto.getPontosRecompensa());
        missao.setBonusSaude(dto.getBonusSaude() != null ? dto.getBonusSaude() : 0);
        missao.setBonusHidratacao(dto.getBonusHidratacao() != null ? dto.getBonusHidratacao() : 0);
        missao.setBonusSono(dto.getBonusSono() != null ? dto.getBonusSono() : 0);
        missao.setBonusExercicio(dto.getBonusExercicio() != null ? dto.getBonusExercicio() : 0);
        missao.setBonusBemEstar(dto.getBonusBemEstar() != null ? dto.getBonusBemEstar() : 0);

        return toResponseDTO(missaoRepository.save(missao));
    }


    @Transactional
    public void desativar(Long id) {
        Missao missao = buscarEntidade(id);
        missao.setAtiva(false);
        missaoRepository.save(missao);
    }


    public Missao buscarEntidade(Long id) {
        return missaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Missão", id));
    }

    public MissaoResponseDTO toResponseDTO(Missao m) {
        return MissaoResponseDTO.builder()
                .id(m.getId())
                .titulo(m.getTitulo())
                .descricao(m.getDescricao())
                .categoria(m.getCategoria())
                .pontosRecompensa(m.getPontosRecompensa())
                .bonusSaude(m.getBonusSaude())
                .bonusHidratacao(m.getBonusHidratacao())
                .bonusSono(m.getBonusSono())
                .bonusExercicio(m.getBonusExercicio())
                .bonusBemEstar(m.getBonusBemEstar())
                .ativa(m.getAtiva())
                .build();
    }
}