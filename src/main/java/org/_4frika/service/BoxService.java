package org._4frika.service;

import org._4frika.domain.Box;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoxService {
    @Autowired
    private BoxRepository boxRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void updateBox(String label) {
        Box box = boxRepository.findOne(1l);

        box.setLabel(label);
        boxRepository.save(box);
    }
}
