# Releasing

Prototype work is intentionally light. Before owner acceptance, run only the
focused Java checks, exact candidate verifier, gallery checks, and disposable
staging comparison needed to get useful visual feedback.

After the owner accepts the candidate:

1. Remove every generated implementation placeholder and retain the accepted
   bounded gallery fixture.
2. Record the accepted integration run and exact candidate identities in
   `provenance/release.json`.
3. Change the provenance status to `owner-accepted-release-candidate` through
   a pull request.
4. Build production JAR, sources JAR, POM, and Gradle module metadata with the
   exact promotion Java/Gradle/BlueMap inputs.
5. Confirm their exact sizes and SHA-256 values still match `gradle.properties`
   and `provenance/release.json`.
6. Run `verifyReleaseCandidate -PreleaseTag=v<version>` with all exact candidate
   JAR Gradle properties.
7. Merge the reviewed commit, create an annotated `v<version>` tag at that
   commit, and let `.github/workflows/release.yml` publish.
8. Compare every downloaded release asset to the locally accepted bytes.
9. Update the private root portfolio, queue, and `workspace.json` in a separate
   orchestration commit.

The tag must exactly equal `v<addon_version>`. No release authorizes production
deployment.

The command sequence and required release-provenance fields are recorded in
[`EXECUTION.md`](EXECUTION.md).
