package com.ntu.igts.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntu.igts.constants.Constants;
import com.ntu.igts.enums.OrderByEnum;
import com.ntu.igts.enums.SortByEnum;
import com.ntu.igts.model.Favorite;
import com.ntu.igts.repository.FavoriteRepository;
import com.ntu.igts.services.FavoriteService;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private FavoriteRepository favoriteRepository;

    @Override
    @Transactional
    public Favorite create(Favorite favorite) {
        return favoriteRepository.create(favorite);
    }

    @Override
    @Transactional
    public Favorite update(Favorite favorite) {
        return favoriteRepository.update(favorite);
    }

    @Override
    @Transactional
    public boolean delete(String favoriteId) {
        favoriteRepository.delete(favoriteId);
        Favorite favorite = favoriteRepository.findById(favoriteId);
        if (favorite == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Favorite getById(String favoriteId) {
        return favoriteRepository.findById(favoriteId);
    }

    @Override
    public List<Favorite> getByUserId(String userId) {
        Map<String, String> criteriaMap = new HashMap<String, String>();
        criteriaMap.put(Constants.FIELD_USERID, userId);
        return favoriteRepository.findAll(SortByEnum.CREATED_TIME, OrderByEnum.DESC, criteriaMap);
    }

    @Override
    public Page<Favorite> getPaginatedFavoritesByUserId(int currentPage, int pageSize, String userId) {
        Map<String, String> criteriaMap = new HashMap<String, String>();
        criteriaMap.put(Constants.FIELD_USERID, userId);
        return favoriteRepository.findByPage(currentPage, pageSize, SortByEnum.CREATED_TIME, OrderByEnum.DESC,
                        criteriaMap);
    }

}
