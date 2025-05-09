package nl.novi.bloomtrail.helper;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlackList {
        private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

        public void blacklist(String token) {
            blacklistedTokens.add(token);
        }

        public boolean isBlacklisted(String token) {
            return blacklistedTokens.contains(token);
        }

}
