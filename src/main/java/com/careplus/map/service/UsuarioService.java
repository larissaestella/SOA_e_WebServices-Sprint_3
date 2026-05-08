package com.careplus.map.service;

import com.careplus.map.exception.RecursoNaoEncontradoException;
import com.careplus.map.exception.RegraNegocioException;
import com.careplus.map.model.dto.*;
import com.careplus.map.model.entity.Avatar;
import com.careplus.map.model.entity.Usuario;
import com.careplus.map.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioCadastroDTO dto) {
        // Verifica unicidade do e-mail
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RegraNegocioException("Já existe um usuário com o e-mail: " + dto.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .dataNascimento(dto.getDataNascimento())
                .build();

        Avatar avatar = Avatar.builder()
                .usuario(usuario)
                .nomeAvatar(dto.getNomeAvatar())
                .nivel(1)
                .pontosTotal(0)
                .saude(0)
                .hidratacao(0)
                .sono(0)
                .exercicio(0)
                .bemEstar(0)
                .build();

        usuario.setAvatar(avatar);

        Usuario salvo = usuarioRepository.save(usuario);
        return toResponseDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = buscarEntidade(id);
        return toResponseDTO(usuario);
    }


    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioAtualizacaoDTO dto) {
        Usuario usuario = buscarEntidade(id);

        // Verifica conflito de e-mail com outro usuário
        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new RegraNegocioException("Já existe um usuário com o e-mail: " + dto.getEmail());
            }
            usuario.setEmail(dto.getEmail());
        }

        if (dto.getNome() != null)            usuario.setNome(dto.getNome());
        if (dto.getDataNascimento() != null)  usuario.setDataNascimento(dto.getDataNascimento());

        return toResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void remover(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.delete(usuario);
    }


    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }

    private UsuarioResponseDTO toResponseDTO(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nome(u.getNome())
                .email(u.getEmail())
                .dataNascimento(u.getDataNascimento())
                .criadoEm(u.getCriadoEm())
                .build();
    }
}