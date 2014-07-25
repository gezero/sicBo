package com.ingg.exercise.sicbo;

import com.ingg.exercise.sicbo.model.ConsoleResultDisplay;
import com.ingg.exercise.sicbo.solution.SicBo;

/**
 * @author iKernel Team
 */
public class Main {

    public static void main(String[] args) {
        new SicBo(new ConsoleResultDisplay()).open();
    }

}
