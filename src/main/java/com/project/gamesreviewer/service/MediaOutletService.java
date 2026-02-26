package com.project.gamesreviewer.service;

import com.project.gamesreviewer.dao.IMediaOutletDAO;
import com.project.gamesreviewer.model.MediaOutlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MediaOutletService {

    private static final Logger logger = LoggerFactory.getLogger(MediaOutletService.class);

    @Autowired
    private IMediaOutletDAO mediaOutletDAO;

    public List<MediaOutlet> getAllMediaOutlets() {
        logger.debug("Fetching all media outlets");
        List<MediaOutlet> outlets = mediaOutletDAO.findAll();
        outlets.sort((o1, o2) -> Integer.compare(o1.id(), o2.id()));
        return outlets;
    }

    public MediaOutlet getMediaOutletById(int id) {
        logger.debug("Fetching media outlet by id: {}", id);
        return mediaOutletDAO.findById(id);
    }

    public int createMediaOutlet(MediaOutlet outlet) {
        logger.debug("Creating media outlet: {}", outlet.name());
        return mediaOutletDAO.create(outlet);
    }

    public void updateMediaOutlet(MediaOutlet outlet) {
        logger.debug("Updating media outlet id: {}", outlet.id());
        mediaOutletDAO.update(outlet);
    }

    public void deleteMediaOutlet(int id) {
        logger.debug("Deleting media outlet id: {}", id);
        mediaOutletDAO.delete(id);
    }
}
