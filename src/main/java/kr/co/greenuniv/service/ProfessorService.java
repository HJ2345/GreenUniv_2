package kr.co.greenuniv.service;


import kr.co.greenuniv.dto.ProfessorDTO;
import kr.co.greenuniv.entity.Department;
import kr.co.greenuniv.entity.Professor;
import kr.co.greenuniv.entity.University;
import kr.co.greenuniv.repository.DeptRepository;
import kr.co.greenuniv.repository.ProfessorRepository;
import kr.co.greenuniv.repository.UnivRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UnivRepository univRepository;
    private final DeptRepository deptRepository;
    private final ModelMapper modelMapper;

    public void regProfessor(ProfessorDTO professorDTO) {

        // 외래키 엔티티 조회
        University university = univRepository.findById(professorDTO.getP_lesson())
                .orElseThrow(() -> new RuntimeException("University not found"));

        log.info("P_spec2 from DTO: {}", professorDTO.getP_spec2());
        log.info("모든 Department 목록: {}", deptRepository.findAll());


        Department department = deptRepository.findByDeptNo(professorDTO.getP_spec2());
        if (department == null) {
            throw new RuntimeException("Department not found");
        }

        // P_num 생성: 연도 + 학과코드 + 순번
        String year = professorDTO.getP_appointdate().substring(0, 4); // ex: 2024
        String deptCode = department.getDeptNo();                      // ex: 01
        String prefix = year + deptCode;                               // ex: 202401

        String latestPnum = professorRepository.findMaxPNumWithPrefix(prefix);
        int nextSeq = 1;
        if (latestPnum != null) {
            String lastSeq = latestPnum.substring(prefix.length());
            nextSeq = Integer.parseInt(lastSeq) + 1;
        }
        String newPnum = prefix + String.format("%02d", nextSeq);

        // 저장
        Professor professor = modelMapper.map(professorDTO, Professor.class);



        professor.setP_num(newPnum);
        professor.setUniversity(university);
        professor.setDepartment(department);

        professorRepository.save(professor);
    }

}

