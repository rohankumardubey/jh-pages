package org.sunix.jhpages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Supplier;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class GitHubServiceTest {

    @Inject
    GitHubService gitHubService;

    @Test
    void should_return_repoName_without_username() {
        String githubPagesProjectRef = "sunix/mywebsite";
        gitHubService.withFullRepoName(githubPagesProjectRef);
        assertEquals(gitHubService.getRepoName(), "mywebsite",
                "the reponame should be the last part of the full repo name");
    }

    @Test
    void should_createGHRepo() {
        String githubPagesProjectRef = "sunix/mywebsite3";
        cleanUpRepoForTest(githubPagesProjectRef);
        createRepoForTest(githubPagesProjectRef);
        assertTrue(gitHubService.checkRepoExist());
    }

    private void assertFalseWith5Retries(Supplier<Boolean> supplier, String errorMessage) {
        for (int i = 0; i < 5; i++) {
            if (supplier.get()) {
                try {
                    Thread.sleep(1000);
                } finally {
                    continue;
                }
            }
            break;
        }
        assertFalse(supplier.get());
    }

    @Test
    void should_createGhpagesBranch() {
        String githubPagesProjectRef = "sunix/mywebsite3";
        cleanUpRepoForTest(githubPagesProjectRef);
        createRepoForTest(githubPagesProjectRef);

        // making sure there is not gh-pages
        assertFalse(gitHubService.checkRemoteGhPagesBranchExist(),
                "after repo creation, gh-pages branch should not be created");

        // create the gh-pages branch
        gitHubService.checkoutGhPagesBranch();

        assertTrue(gitHubService.checkLocalGhPagesBranchExist(),
                "after checkout, gh-pages branch should have been created");

        // if (gitHubService.checkGhPagesBranchExist()) {
        // display.updateText("Branch gh-pages for repo " +
        // gitHubService.getFullRepoName() + " already exist");

        // gitHubService.checkoutGhPagesBranch();
    }

    private void createRepoForTest(String githubPagesProjectRef) {
        assertFalseWith5Retries(() -> gitHubService.checkRepoExist(),
                "The repo " + githubPagesProjectRef + " should not exist in the test context");
        gitHubService.createRepo();
    }

    private void cleanUpRepoForTest(String githubPagesProjectRef) {

        gitHubService.withFullRepoName(githubPagesProjectRef) //
                .init();
        // remove the repo first before starting the test
        if (gitHubService.checkRepoExist()) {
            gitHubService.deleteRepo();
        }
    }

}