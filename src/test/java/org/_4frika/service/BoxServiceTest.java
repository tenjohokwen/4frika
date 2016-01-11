package org._4frika.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org._4frika.SampleDataJpaApplication;
import org._4frika.domain.Box;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SampleDataJpaApplication.class})
public class BoxServiceTest {

    @Autowired
    private BoxService boxService;

    @Autowired
    private BoxRepository boxRepository;

    @Test
    public void testUpdateBox() throws Exception {
        List<Box> boxes = (List<Box>) boxRepository.findAll();
        int initialCount = boxes.size();
        boxRepository.delete(5l);
        boxes = (List<Box>) boxRepository.findAll();
        assertTrue(boxes.size() == (initialCount - 1));
    }

}