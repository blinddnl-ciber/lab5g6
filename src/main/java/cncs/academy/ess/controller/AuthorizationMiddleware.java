package cncs.academy.ess.controller;

import cncs.academy.ess.repository.UserRepository;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.http.UnauthorizedResponse;
import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;


public class AuthorizationMiddleware implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationMiddleware.class);
    private final UserRepository userRepository;
    private final Enforcer enforcer;
    private static final String ISSUER = "o back-end";
    private static final String JWT_SECRET = "secret-muito-importante-do-jwt";

    public AuthorizationMiddleware(UserRepository userRepository, Enforcer enforcer) {
        this.userRepository = userRepository;
        this.enforcer = enforcer;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        // if method is OPTIONS bypass auth middleware
        if (ctx.method() == HandlerType.OPTIONS) {
            // Optionally: validate if it is a legitimate CORS preflight
            return;
        }

        // Allow unauthenticated requests to /login
        if (ctx.path().equals("/login") && ctx.method().name().equals("POST"))
            return;

        // Check if authorization header exists
        String authorizationHeader = ctx.header("Authorization");
        String path = ctx.path();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Authorization header is missing or invalid '{}' for path '{}'", authorizationHeader, path);
            throw new UnauthorizedResponse();
        }

        // Extract token from authorization header
        String token = authorizationHeader.substring(7); // Remove "Bearer "

        // Check if token is valid (perform authentication logic)
        int userId = validateTokenAndGetUserId(token);
        if (!checkAccessControl(ctx,userId)) {
            throw new UnauthorizedResponse("Access denied, wrong role!");
        }

        // Add user ID to context for use in route handlers
        ctx.attribute("userId", userId);
    }

    private boolean checkAccessControl (Context ctx, int userId){
        String path = ctx.path();
        String method = ctx.method().name();
        String username = userRepository.findById(userId).getUsername();
        logger.info("Path : {}, Method : {}, Username : {}", path, method, username);
        return enforcer.enforce(username, path, method);
    }

    private Integer validateTokenAndGetUserId(String token) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);

            return decodedJWT.getClaim("id").asInt();

        } catch (JWTVerificationException e) {
            logger.info("Token Inválido:", e.getMessage());
            return -1;
        }
    }
}

