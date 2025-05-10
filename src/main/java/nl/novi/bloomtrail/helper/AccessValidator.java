package nl.novi.bloomtrail.helper;

import nl.novi.bloomtrail.exceptions.BadRequestException;
import nl.novi.bloomtrail.exceptions.ForbiddenException;
import nl.novi.bloomtrail.models.CoachingProgram;
import nl.novi.bloomtrail.models.Session;
import nl.novi.bloomtrail.models.Step;
import nl.novi.bloomtrail.repositories.CoachingProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AccessValidator {

    @Autowired
    private CoachingProgramRepository coachingProgramRepository;


    public void validateAuthority(String authorityName) throws BadRequestException {
        List<String> validRoles = Arrays.asList("ROLE_USER", "ROLE_COACH", "ROLE_ADMIN");
        if (!validRoles.contains(authorityName)) {
            throw new BadRequestException("Invalid role: " + authorityName);
        }
    }

    public String getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("No authenticated user.");
        }
        return auth.getName();
    }

    public void validateSelfOrAffiliatedCoachOrAdminAccess(String username, List<CoachingProgram> programs) {
        String currentUsername = getAuthenticatedUsername();
        boolean isAdmin = isAdmin();
        boolean isSelf = currentUsername.equals(username);
        boolean isCoach = programs.stream()
                .anyMatch(program -> program.getCoach().getUsername().equals(currentUsername));

        if (!isAdmin && !isSelf && !isCoach) {
            throw new AccessDeniedException("You are not allowed to access this user's coaching program.");
        }
    }

    public void validateCoachOrAdminAccess(Session session) {
        String username = getAuthenticatedUsername();
        boolean isAdmin = isAdmin();
        boolean isCoach = username.equals(session.getCoach());

        if (!isAdmin && !isCoach) {
            throw new AccessDeniedException("Only the assigned coach or an admin can perform this action.");
        }
    }

    public void validateCoachOrClientOrAdminAccess(Session session) {
        String username = getAuthenticatedUsername();
        boolean isAdmin = isAdmin();

        boolean isCoach = username.equals(session.getCoach());
        boolean isClient = username.equals(session.getClient());

        if (!isAdmin && !isCoach && !isClient) {
            throw new AccessDeniedException("Access denied: You are not allowed to access this session.");
        }
    }

    public void validateCoachOwnsProgramOrIsAdmin(CoachingProgram program) {
        String username = getAuthenticatedUsername();
        boolean isAdmin = isAdmin();
        boolean isCoach = program.getCoach().getUsername().equals(username);

        if (!isAdmin && !isCoach) {
            throw new AccessDeniedException("You do not have permission to modify this coaching program.");
        }
    }

    public void validateClientOrCoachOrAdminAccess(CoachingProgram program) {
        String username = getAuthenticatedUsername();
        boolean isAdmin = isAdmin();


        boolean isCoach = program.getCoach().getUsername().equals(username);
        boolean isClient = program.getClient().getUsername().equals(username);

        if (!isAdmin && !isCoach && !isClient) {
            throw new AccessDeniedException("Access denied: You are not affiliated with this coaching program.");
        }
    }

    public void validateSelfOrAdminAccess(String targetUsername) {
        String username = getAuthenticatedUsername();
        boolean isAdmin = isAdmin();
        boolean isSelf = username.equals(targetUsername);

        if (!isAdmin && !isSelf) {
            throw new ForbiddenException("Access denied: You can only access your own data unless you're an admin.");
        }
    }

    public void validateCoachOwnsStepOrAdmin(Step step) {
        String username = getAuthenticatedUsername();
        boolean isAdmin = isAdmin();

        CoachingProgram program = step.getCoachingProgram();
        boolean isCoach = program.getCoach().getUsername().equals(username);

        if (!isAdmin && !isCoach) {
            throw new AccessDeniedException("Access denied: only the assigned coach or an admin may perform this action.");
        }
    }

    private Authentication getAuthOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("No authenticated user.");
        }
        return auth;
    }

    private String getAuthenticatedUsername() {
        return getAuthOrThrow().getName();
    }

    public boolean isAdmin() {
        return getAuthOrThrow().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }

    public boolean isCoach() {
        return getAuthOrThrow().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("COACH"));
    }
}
