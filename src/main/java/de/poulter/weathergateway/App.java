/*
 * Weathergateway
 *
 * Copyright (C) 2019 Christian Poulter
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package de.poulter.weathergateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Christian Poulter <devel@poulter.de>
 */
@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:weathergateway.properties")
public class App {
    
    public static void main(String[] args) {
        System.out.println("");
        System.out.println("===============================================================");
        System.out.println("");
        System.out.println("    Weathergateway Copyright (C) 2019 Christian Poulter"); 
        System.out.println("    This program comes with ABSOLUTELY NO WARRANTY. This");
        System.out.println("    is free software, and you are welcome to redistribute");
        System.out.println("    it under certain conditions.");
        System.out.println("");
        System.out.println("===============================================================");
        System.out.println("");
        
        SpringApplication.run(App.class, args);
    }
}