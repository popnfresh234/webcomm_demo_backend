package com.example.apilogin.services;

import com.example.apilogin.entities.PasswordResetEntity;
import com.example.apilogin.repositories.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {
    private final PasswordResetRepository passwordResetRepository;

    public PasswordResetService(PasswordResetRepository passwordResetRepository) {
        this.passwordResetRepository = passwordResetRepository;
    }

    public void save(PasswordResetEntity passwordResetEntity) {
        passwordResetRepository.save(passwordResetEntity);
    }

    public void delete(PasswordResetEntity passwordResetEntity) {
        passwordResetRepository.delete(passwordResetEntity);
    }
}
