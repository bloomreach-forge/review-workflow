package org.bloomreach.forge.reviewworkflow.cms.reviewedactions;

import java.util.Set;

/**
 * Interface for dynamically providing assignable group names in the internal assign dialog's dropdown
 */
public interface AssignableGroupsProvider {
    /**
     * @param currentUserId Currently logged in user's user id
     * @return Set of group Ids
     */
    Set<String> provideGroups(final String currentUserId);
}
