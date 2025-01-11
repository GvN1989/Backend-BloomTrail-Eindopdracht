package nl.novi.bloomtrail.services;

import nl.novi.bloomtrail.models.Step;

public class StrengthProgramService {

    public double calculateProgressPercentage() {
        if (timeline.isEmpty()) {
            return 0.0; // No steps, no progress
        }

        long completedSteps = timeline.stream()
                .filter(Step::getCompleted) // Filter completed steps
                .count();

        return (double) completedSteps / timeline.size() * 100; // Calculate percentage
    }
}
