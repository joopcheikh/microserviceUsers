package com.registration.registration.filter;

import com.registration.registration.service.JwtService;
import com.registration.registration.service.UserDetailsServiceImp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtService jwtService;
    private UserDetailsServiceImp userDetailsServiceImp;

    @Autowired
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsServiceImp userDetailsServiceImp) {
        this.jwtService = jwtService;
        this.userDetailsServiceImp = userDetailsServiceImp;
    }

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String autHeader = request.getHeader("Authorization");

        // "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." : exemple de token d'authentification
        if(autHeader == null || !autHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = autHeader.substring(7);
        String username = jwtService.extractUsername(token);

        /* vérifier si le username n'est pas null et s'il ne s'est pas encore authentifier
         * SecurityContextHolder : permet de vérifier si le user s'est authentifier ou pas
         */
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsServiceImp.loadUserByUsername(username); // load the username

            //vérifier si le token est valide ou pas
            if(jwtService.isTokenValid(token, userDetails)){

                /*
                 * Créer un objet d'authentication pour le user euthentifié
                 * userDetails: représente les détails du user authentifié
                 * null: représente le credential qui n'est pas nécessaire à ce niveau
                 * userDetails.getAuthorities() : obtenir les autorisations du user
                 */
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                /*
                 * définit les détails du jeton d'authentication avec les
                 *  détails de la demande web actuelle
                 */

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                /*
                 * Définit l'objet d'authentification (authToken) dans
                 * SecurityContextHolder, marquant ainsi l'utilisateur
                 * comme authentifié pour la requête en cours
                 */
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }

        /*
         * Procède à la chaîne de filtrage, permettant à la demande de
         * poursuivre le traitement.
         */
        filterChain.doFilter(request, response);
    }

}
