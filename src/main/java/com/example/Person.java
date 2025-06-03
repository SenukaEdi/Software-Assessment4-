package com.example;

import java.util.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.io.*;

public class Person {
    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private HashMap<Date, Integer> demeritPoints; 
    private boolean isSuspended; 

    // Constructor to initialize a person
    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.demeritPoints = new HashMap<>();
        this.isSuspended = false;
    }

    // Method to add a person to a TXT file, after validating input
    public boolean addPerson() {
        // Validate personID format
        if (!personID.matches("^[2-9][0-9].{1,5}[^a-zA-Z0-9]{2,}.{0,2}[A-Z]{2}$")) {
            return false; // Fail if personID doesn't match rules
        }

        // Split address by "|" and validate structure
        String[] addressParts = address.split("\\|");
        // Address must be valid and state must be "Victoria"
        if (addressParts.length != 5 || !addressParts[3].equals("Victoria")) {
            return false; 
        }

        // Validate birthdate format
        if (!birthdate.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return false;
        }

        // Append person info to persons.txt if all checks pass
        try (FileWriter fw = new FileWriter("persons.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(personID + "," + firstName + "," + lastName + "," + address + "," + birthdate);
            bw.newLine(); // Move to new line
            return true; // Successfully added
        } catch (IOException e) {
            return false; // Fail on file I/O error
        }
    }

    // Method to update person's details, with rules and validation
    public boolean updatePersonalDetails(String newID, String newFirstName, String newLastName, String newAddress, String newBirthdate) {
        boolean isUnder18 = getAge(birthdate) < 18; // Check current age
        boolean changingBirthday = !newBirthdate.equals(birthdate); // Is birthday being changed
        boolean idStartsWithEven = Character.getNumericValue(personID.charAt(0)) % 2 == 0;

        // Rule: If birthday is changing, no other field can be changed
        if (changingBirthday &&
            (!newID.equals(personID) || !newFirstName.equals(firstName) || !newLastName.equals(lastName) || !newAddress.equals(address))) {
            return false;
        }

        // Rule: If under 18, address cannot change
        if (isUnder18 && !newAddress.equals(address)) {
            return false;
        }

        // Rule: If first digit of ID is even, ID cannot change
        if (idStartsWithEven && !newID.equals(personID)) {
            return false;
        }

        // Re-validate all fields similar to addPerson()
        if (!newID.matches("^[2-9][0-9].{1,5}[^a-zA-Z0-9]{2,}.{0,2}[A-Z]{2}$")) {
            return false;
        }

        String[] addressParts = newAddress.split("\\|");
        if (addressParts.length != 5 || !addressParts[3].equals("Victoria")) {
            return false;
        }

        if (!newBirthdate.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return false;
        }

        // Apply changes if all conditions are met
        personID = newID;
        firstName = newFirstName;
        lastName = newLastName;
        address = newAddress;
        birthdate = newBirthdate;

        // File update logic can be added here if needed
        return true;
    }

    // Method to add demerit points for an offense date
    public String addDemeritPoints(String offenseDate, int points) {
        // Check format of offense date and points range
        if (!offenseDate.matches("\\d{2}-\\d{2}-\\d{4}") || points < 1 || points > 6) {
            return "Failed";
        }

        try {
            // Parse the offense date into a Date object
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(offenseDate);

            // Add to internal record
            demeritPoints.put(date, points);

            // Calculate total demerit points within last 2 years
            int totalPoints = 0;
            Date now = new Date();

            for (Map.Entry<Date, Integer> entry : demeritPoints.entrySet()) {
                long diff = now.getTime() - entry.getKey().getTime();
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                if (days <= 730) { // Only consider offenses from the past 2 years
                    totalPoints += entry.getValue();
                }
            }

            // Suspension rules based on age
            int age = getAge(birthdate);
            if ((age < 21 && totalPoints > 6) || (age >= 21 && totalPoints > 12)) {
                isSuspended = true; // Set suspended if threshold exceeded
            }

            // Append offense record to file
            try (FileWriter fw = new FileWriter("demeritPoints.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(personID + "," + offenseDate + "," + points);
                bw.newLine();
            }

            return "Success"; // If all passed
        } catch (Exception e) {
            return "Failed"; // On any error
        }
    }

    private int getAge(String birthdateStr) {
        try {
            // Parse birthdate string
            Date birthDate = new SimpleDateFormat("dd-MM-yyyy").parse(birthdateStr);
            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);

            Calendar now = Calendar.getInstance();

            // Compute age by subtracting years
            int age = now.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

            // Adjust if birth date hasn’t occurred yet this year
            if (now.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (Exception e) {
            return 0; // Return 0 on failure to parse
        }
    }
}