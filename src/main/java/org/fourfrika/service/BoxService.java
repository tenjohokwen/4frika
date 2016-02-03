package org.fourfrika.service;

import org.fourfrika.domain.Box;
import org.fourfrika.repository.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class BoxService {
    @Autowired
    private BoxRepository boxRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void updateBox(String label) {
        Box box = boxRepository.findOne(1L);

        box.setLabel(label);
        boxRepository.save(box);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void delete(Long id) {
        Box box = boxRepository.findOne(id);
        if(box!=null) {
            box.setDeleted(1);
        } else {
            throw new EntityNotFoundException("Attempt to delete non-existent entity");
        }
    }

}
