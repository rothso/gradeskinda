package com.rothanak.gradeskinda.domain.model;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Not sure if this test offers any real business value. The equals and hashcode are overridden
 * just for particular unit test verifications involving equality.
 */
public class CredentialsTest {

    @Test public void equals_MatchesHashCodeEquals() {
        EqualsVerifier.forClass(Credentials.class).verify();
    }

    @Test public void sameCredentials_AreEqual() {
        Credentials credentials1 = CredentialsBuilder.defaultCredentials().build();
        Credentials credentials2 = CredentialsBuilder.defaultCredentials().build();

        assertThat(credentials1).isEqualTo(credentials1);
        assertThat(credentials1).isEqualTo(credentials2);
    }

    @Test public void differentCredentials_AreUnequal() {
        // Exhaustively test four cross-combination pairs
        Credentials credentials1 = CredentialsBuilder.defaultCredentials()
                .withUsername("UsernameA").withPassword("PasswordA").build();
        Credentials credentials2 = CredentialsBuilder.defaultCredentials()
                .withUsername("UsernameA").withPassword("PasswordB").build();
        Credentials credentials3 = CredentialsBuilder.defaultCredentials()
                .withUsername("UsernameB").withPassword("PasswordA").build();
        Credentials credentials4 = CredentialsBuilder.defaultCredentials()
                .withUsername("UsernameB").withPassword("PasswordB").build();

        assertThat(credentials1)
                .isNotEqualTo(credentials2)
                .isNotEqualTo(credentials3)
                .isNotEqualTo(credentials4);
    }

}