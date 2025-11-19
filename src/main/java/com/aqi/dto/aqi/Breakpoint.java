package com.aqi.dto.aqi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Breakpoint {
    double lowConc;
    double highConc;
    int lowIndex;
    int highIndex;
}
