package practise.patients;

import practise.Practise;
import practise.patients.patientFiles.Patient;
import practise.patients.patientFiles.PatientFileHandler;
import practise.patients.treatment.Rooms;
import practise.patients.treatment.Treatment;
import practise.patients.treatment.TreatmentType;
import practise.stock.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class PatientHandler {
    final PatientFileHandler patientFileHandler = new PatientFileHandler();
    private final ArrayList<TreatmentType> treatments = new ArrayList<>();
    private final Practise practise;
    private ArrayList<Treatment> runningTreatments = new ArrayList<>();
    //private ArrayList<Rooms> roomHandler = new ArrayList<>();

    public PatientHandler(Practise practise) {
        this.practise = practise;
    }

    /**
     * Creates a new type of treatment
     *
     * @param name Name of treatment
     * @param cost Cost of treatment
     */
    public void addTreatmentType(String name, double cost, HashMap<Item, Integer> needs) throws Exception {
        if (treatments.stream().filter(t -> t.getName() == name).collect(Collectors.toList()).size() > 0)
            throw new Exception("Treatment type name already in use");
        treatments.add(new TreatmentType(name, cost, needs));
        //TODO check if treatment name is already in use
    }

    /**
     * Removes a type of treatment
     *
     * @param index Treatment index, use viewTreatments to view indexes
     */
    public void removeTreatmentType(int index) {
        treatments.remove(index);
    }

    /**
     * Starts new treatment, closes Room, takes needed items from stock and occupies room
     *
     * @param treatmentIndex Index of treatment type to do
     * @param patient        Patient to be treated
     */

    public void startTreatment(int treatmentIndex, Patient patient, ArrayList<Rooms> roomHandler) throws Exception {
        runningTreatments.add(new Treatment(treatments.get(treatmentIndex), patient));
        // takes needed items from stock
        treatments.get(treatmentIndex).getNeeds().forEach((key, val) -> {
            try {
                practise.getStockHandler().take(key, val);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        for (int i = 0; i < roomHandler.size(); i++) {
            if (roomHandler.get(i).isOpen() && roomHandler.get(i).getTreatmenttype().equals(treatments.get(treatmentIndex))) {
                roomHandler.get(i).closeRoom();
            }

        }
    }

    /**
     * Ends running treatment, frees up room and increases budget by treatment cost
     *
     * @param index Index of treatment to be ended
     */
    public void endTreatment(int index, ArrayList<Rooms> roomHandler) {
        practise.increaseBudget(treatments.get(index).getCost());
        runningTreatments.remove(index);
        for (int i = 0; i < roomHandler.size(); i++) {
            if (!roomHandler.get(i).isOpen() && roomHandler.get(i).getTreatmenttype().equals(treatments.get(index))) {
            } else {
                roomHandler.get(i).openRoom();
            }
        }
    }

    /**
     * Displays all Treatment names and costs with their index
     */
    public void viewTreatmentTypes() {
        int i = 0;
        for (TreatmentType t : treatments) {
            System.out.println(i + ": " + t.getName() + ", " + t.getCost());
            i++;
        }
    }

    /**
     * Displays all running treatments; index, name of patients and the type of treatment they get
     */
    public void viewRunningTreatments() {
        int i = 0;
        for (Treatment t : runningTreatments) {
            practise.logger.info(String.format("%s: %s, %s",
                    (i++), t.getPatient().getName(), t.getTreatmentType().getName()));
        }
    }

    public TreatmentType getTreatmenttype(String name) throws Exception {
        TreatmentType t = null;
        for (int i = 0; i < treatments.size(); i++) {
            if (treatments.get(i).getName().trim().toLowerCase().equals(name.trim().toLowerCase())) {
                t = treatments.get(i);
                if (t == null) throw new Exception("Name missing.");
            }
        }
        return t;
    }

    public ArrayList<TreatmentType> getTreatmentTypes() {
        return treatments;
    }

    public ArrayList<Treatment> getRunningTreatments() {
        return runningTreatments;
    }

    public PatientFileHandler getPatientFileHandler() {
        return patientFileHandler;
    }
}