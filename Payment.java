package model;

/** Record of a completed payment. */
public class Payment {
    private final Contract contract;
    private final String methodName;

    public Payment(Contract contract, String methodName) {
        this.contract = contract;
        this.methodName = methodName;
    }

    @Override
    public String toString() {
        return "Paid Rs." + contract.getAmount() + " to " + contract.getFreelancer().getName()
             + " for '" + contract.getProject().getTitle() + "' via " + methodName;
    }
}
