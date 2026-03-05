package com.project.gamereviewer.service;

import com.project.gamereviewer.dto.request.MediaOutletCreateRequest;
import com.project.gamereviewer.dto.response.MediaOutletResponse;
import com.project.gamereviewer.entity.MediaOutlet;
import com.project.gamereviewer.exception.DuplicateResourceException;
import com.project.gamereviewer.exception.ResourceNotFoundException;
import com.project.gamereviewer.mapper.MediaOutletMapper;
import com.project.gamereviewer.repository.MediaOutletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MediaOutletService {

    public static final String RESOURSE_NAME = "MediaOutlet";
    public static final String RESOURSE_NAME_FIELD_NAME = "name";
    
    private final MediaOutletRepository mediaOutletRepository;
    private final MediaOutletMapper mediaOutletMapper;
    
    public Page<MediaOutletResponse> getAllMediaOutlets(Pageable pageable) {
        return mediaOutletRepository.findAll(pageable)
            .map(mediaOutletMapper::toResponse);
    }
    
    public MediaOutletResponse getMediaOutletById(Integer id) {
        return mediaOutletRepository.findById(id)
            .map(mediaOutletMapper::toResponse)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
    }
    
    @Transactional
    public MediaOutletResponse createMediaOutlet(MediaOutletCreateRequest request) {
        if (mediaOutletRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(RESOURSE_NAME, RESOURSE_NAME_FIELD_NAME, request.name());
        }
        
        MediaOutlet mediaOutlet = mediaOutletMapper.toEntity(request);
        MediaOutlet saved = mediaOutletRepository.save(mediaOutlet);
        return mediaOutletMapper.toResponse(saved);
    }
    
    @Transactional
    public MediaOutletResponse updateMediaOutlet(Integer id, MediaOutletCreateRequest request) {
        MediaOutlet mediaOutlet = mediaOutletRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(RESOURSE_NAME, id));
        
        if (!mediaOutlet.getName().equals(request.name()) && mediaOutletRepository.existsByName(request.name())) {
            throw new DuplicateResourceException(RESOURSE_NAME, RESOURSE_NAME_FIELD_NAME, request.name());
        }
        
        mediaOutlet.setName(request.name());
        mediaOutlet.setWebsiteUrl(request.websiteUrl());
        mediaOutlet.setFoundedYear(request.foundedYear());
        
        MediaOutlet updated = mediaOutletRepository.save(mediaOutlet);
        return mediaOutletMapper.toResponse(updated);
    }
    
    @Transactional
    public void deleteMediaOutlet(Integer id) {
        if (!mediaOutletRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESOURSE_NAME, id);
        }
        mediaOutletRepository.deleteById(id);
    }
}
