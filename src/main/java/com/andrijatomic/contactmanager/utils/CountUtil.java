package com.andrijatomic.contactmanager.utils;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

public class CountUtil {
  public static <T, ID> ResponseEntity<?> count (JpaRepository<T, ID> repository) {
    long count = repository.count();
    if (count == 0) {
      return ResponseEntity.notFound().build();
    }
    Map<String, Long> response = new HashMap<>();
    response.put("count", count);
    return ResponseEntity.ok(response);
  }
}
