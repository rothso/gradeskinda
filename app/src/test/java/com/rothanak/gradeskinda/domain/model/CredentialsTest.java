package com.rothanak.gradeskinda.domain.model;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class CredentialsTest {

    @Test public void equals_MatchesHashCodeEquals() {
        EqualsVerifier.forClass(Credentials.class).verify();
    }

    @Test public void sameCredentials_AreEqual() {
        Credentials credentials1 = new Credentials("User", "Pass");
        Credentials credentials2 = new Credentials("User", "Pass");

        assertThat(credentials1).isEqualTo(credentials1);
        assertThat(credentials1).isEqualTo(credentials2);
    }

    @Test public void differentCredentials_AreUnequal() {
        Credentials credentials1 = new Credentials("User", "Pass");
        Credentials credentials2 = new Credentials("Diff", "Pass");
        Credentials credentials3 = new Credentials("User", "Diff");
        Credentials credentials4 = new Credentials("Diff", "Diff");

        assertThat(credentials1)
                .isNotEqualTo(credentials2)
                .isNotEqualTo(credentials3)
                .isNotEqualTo(credentials4);
    }

}