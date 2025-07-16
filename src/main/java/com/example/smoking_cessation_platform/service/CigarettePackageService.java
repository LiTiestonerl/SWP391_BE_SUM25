package com.example.smoking_cessation_platform.service;

import com.example.smoking_cessation_platform.dto.CigarettePackage.CigarettePackageDTO;
import com.example.smoking_cessation_platform.entity.CigarettePackage;
import com.example.smoking_cessation_platform.exception.ResourceNotFoundException;
import com.example.smoking_cessation_platform.mapper.CigarettePackageMapper;
import com.example.smoking_cessation_platform.repository.CigarettePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CigarettePackageService {
    @Autowired
    CigarettePackageRepository cigarettePackageRepository;

    @Autowired
    CigarettePackageMapper cigarettePackageMapper;

    public List<CigarettePackageDTO> getAllPackage() {
        return cigarettePackageRepository.findAll().stream()
                .map(cigarettePackageMapper:: toDTO)
                .collect(Collectors.toList());
    }


    public CigarettePackageDTO createPackage(CigarettePackageDTO dto) {
        // Chuyển DTO → Entity
        CigarettePackage pkg = cigarettePackageMapper.toEntity(dto);

        // Xoá id để đảm bảo Hibernate INSERT thay vì MERGE
        pkg.setCigaretteId(null);

        // Lưu entity và trả DTO về
        CigarettePackage saved = cigarettePackageRepository.save(pkg);
        return cigarettePackageMapper.toDTO(saved);
    }


    public void deletePackage(Long id) {
        if (!cigarettePackageRepository.existsById(id)) {
            throw new ResourceNotFoundException("CigarettePackage", id);
        }
        cigarettePackageRepository.deleteById(id);
    }


    public CigarettePackage getPackageId(Long fromPackageId) {
        return cigarettePackageRepository.findById(fromPackageId)
                .orElseThrow(() -> new ResourceNotFoundException("CigarettePackage", fromPackageId));
    }

    public CigarettePackageDTO updatePackage(Long id, CigarettePackageDTO dto) {
        // 1. Tìm entity hiện có; nếu không thấy thì ném lỗi
        CigarettePackage pkg = cigarettePackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CigarettePackage", id));

        // 2. Ghi đè các thuộc tính (full‑update theo chuẩn PUT)
        pkg.setCigaretteName(dto.getCigaretteName());
        pkg.setPrice(dto.getPrice());
        pkg.setBrand(dto.getBrand());
        pkg.setNicoteneStrength(dto.getNicoteneStrength());
        pkg.setFlavor(dto.getFlavor());
        pkg.setSticksPerPack(dto.getSticksPerPack());
        pkg.setNicotineMg(dto.getNicotineMg());

        // 3. Lưu lại DB
        CigarettePackage saved = cigarettePackageRepository.save(pkg);

        // 4. Chuyển sang DTO trả về
        return cigarettePackageMapper.toDTO(saved);
    }

    public CigarettePackageDTO getByIdDTO(Long id) {
        // 1. Tìm kiếm gói thuốc theo ID
        CigarettePackage pkg = cigarettePackageRepository.findById(id)
                // 2. Nếu không tìm thấy thì ném lỗi ResourceNotFoundException
                .orElseThrow(() -> new ResourceNotFoundException("CigarettePackage", id));

        // 3. Chuyển entity thành DTO rồi trả về
        return cigarettePackageMapper.toDTO(pkg);
    }

    // ✅ Thêm hàm này để dùng ở RecommendationService
    public List<CigarettePackage> getAllPackages() {
        return cigarettePackageRepository.findAll();
    }
}
