package model;

/** Project lifecycle: Open -> Assigned -> In Progress -> Completed -> Paid. */
public enum ProjectStatus {
    OPEN, ASSIGNED, IN_PROGRESS, COMPLETED, PAID;

    /** Returns the only stage allowed after this one (null after PAID). */
    public ProjectStatus next() {
        ProjectStatus[] all = values();
        return ordinal() + 1 < all.length ? all[ordinal() + 1] : null;
    }
}
