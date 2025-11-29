package lk.mc.core.controllers;

/**
 * It is required to initialize all dependent module facades by assigning entity managers to handle transactions.
 * TsActiveApiController define core API for session beans.
 * Note : Added this as an abstract class due to issues when resolve session been which has implemented an interface
 * from an external module
 *
 * @author vihanga
 * @since 27/10/2021
 * MC-lms
 */
public abstract class TsActiveApiController {
    /**
     * Initialize facades by setting entity manager for handle JPA transactions. When system initialize, call this method
     */
    public abstract void initializeFacades();
}
