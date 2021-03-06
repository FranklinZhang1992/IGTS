package com.ntu.igts.services.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntu.igts.model.SensitiveWord;
import com.ntu.igts.repository.SensitiveWordRepository;
import com.ntu.igts.services.SensitiveWordService;

@Service
public class SensitiveWordServiceImpl implements SensitiveWordService {

    @Resource
    private SensitiveWordRepository sensitiveWordRepository;

    @Override
    @Transactional
    public SensitiveWord create(SensitiveWord sensitiveWord) {
        return sensitiveWordRepository.create(sensitiveWord);
    }

    @Override
    @Transactional
    public SensitiveWord update(SensitiveWord sensitiveWord) {
        return sensitiveWordRepository.update(sensitiveWord);
    }

    @Override
    @Transactional
    public boolean delete(String sensitiveWordId) {
        sensitiveWordRepository.delete(sensitiveWordId);
        SensitiveWord sensitiveWord = sensitiveWordRepository.findById(sensitiveWordId);
        if (sensitiveWord == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SensitiveWord getById(String sensitiveWordId) {
        return sensitiveWordRepository.findById(sensitiveWordId);
    }

    @Override
    public List<SensitiveWord> getAll() {
        return sensitiveWordRepository.getAllActiveSensitiveWords();
    }

    @Override
    public boolean isSensitiveWord(String sensitiveWord) {
        List<SensitiveWord> sensitiveWords = sensitiveWordRepository.getByWord(sensitiveWord);
        if (sensitiveWords.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

}
