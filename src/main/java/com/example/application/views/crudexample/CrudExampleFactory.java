package com.example.application.views.crudexample;

import com.example.application.services.SamplePersonService;
import com.vaadin.flow.function.SerializableRunnable;
import org.springframework.stereotype.Component;

@Component
class CrudExampleFactory {

    private final SamplePersonService samplePersonService;

    public CrudExampleFactory(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
    }

    PersonForm createForm(SerializableRunnable refreshGridRunnable) {
        return new PersonForm(samplePersonService, refreshGridRunnable);
    }

    SamplePersonService createService() {
        return samplePersonService;
    }

    public PersonGrid createGrid(SerializableRunnable clearForm) {
        return new PersonGrid(samplePersonService, clearForm);
    }
}
