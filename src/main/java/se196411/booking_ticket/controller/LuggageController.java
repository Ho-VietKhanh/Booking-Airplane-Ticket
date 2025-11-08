package se196411.booking_ticket.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se196411.booking_ticket.model.entity.LuggageEntity;
import se196411.booking_ticket.service.LuggageService;

import java.util.List;

@RestController
@RequestMapping("/api/luggage")
public class LuggageController {

    @Autowired
    private LuggageService luggageService;

    @PostMapping("/create")
    public ResponseEntity<LuggageEntity> createLuggage(@RequestBody LuggageEntity luggage) {
        LuggageEntity createdLuggage = luggageService.create(luggage);
        return ResponseEntity.ok(createdLuggage);
    }

    @GetMapping("/getById/{luggageId}")
    public ResponseEntity<LuggageEntity> getLuggageById(@PathVariable Integer luggageId) {
        LuggageEntity luggage = luggageService.findById(luggageId);
        if (luggage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(luggage);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<LuggageEntity>> getAllLuggages() {
        List<LuggageEntity> luggages = luggageService.findAll();
        return ResponseEntity.ok(luggages);
    }

    @PutMapping("/update/{luggageId}")
    public ResponseEntity<LuggageEntity> updateLuggage(@PathVariable Integer luggageId, @RequestBody LuggageEntity luggage) {
        LuggageEntity existingLuggage = luggageService.findById(luggageId);
        if (existingLuggage == null) {
            return ResponseEntity.notFound().build();
        }
        LuggageEntity updatedLuggage = luggageService.update(luggageId, luggage);
        return ResponseEntity.ok(updatedLuggage);
    }

    @DeleteMapping("/delete/{luggageId}")
    public ResponseEntity<String> deleteLuggage(@PathVariable Integer luggageId) {
        LuggageEntity luggage = luggageService.findById(luggageId);
        if (luggage == null) {
            return ResponseEntity.notFound().build();
        }
        luggageService.deleteById(luggageId);
        return ResponseEntity.ok("Luggage deleted successfully");
    }
}


