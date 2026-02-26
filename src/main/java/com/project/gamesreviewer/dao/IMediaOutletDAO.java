package com.project.gamesreviewer.dao;

import com.project.gamesreviewer.model.MediaOutlet;

import java.util.List;

public interface IMediaOutletDAO {
    List<MediaOutlet> findAll();
    MediaOutlet findById(int id);
    int create(MediaOutlet outlet);
    void update(MediaOutlet outlet);
    void delete(int id);
}
