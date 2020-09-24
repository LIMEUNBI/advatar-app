package com.epopcon.advatar.common.util.event;

public abstract class EventHandler {

    public final static int STATUS_READY = 0;
    public final static int STATUS_RUNNING = 1;
    public final static int STATUS_COMPLETE = 2;

    protected boolean interrupted = false;
    protected int status = STATUS_READY;

    private Object currentStep = null;

    public boolean isExecutable(Event event) {
        return true;
    }

    public void execute(Event event) {

        if (interrupted) {
            return;
        }
        status = STATUS_RUNNING;
        onEvent(event);
        status = STATUS_COMPLETE;
    }

    public abstract void onEvent(Event event);

    public void onInterrupted() {

    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public int getStatus() {
        return status;
    }

    public Object getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Object currentStep) {
        this.currentStep = currentStep;
    }
}
