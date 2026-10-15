package com.university.registry.ui;

import com.university.registry.exception.DuplicateEntityException;
import com.university.registry.exception.EntityNotFoundException;
import com.university.registry.exception.RegistryException;
import com.university.registry.model.Professor;
import com.university.registry.service.EnrollmentService;
import com.university.registry.service.ProfessorService;

import java.util.Collection;

public class ProfessorMenu extends BaseMenu
{
    private final ProfessorService professorService;
    private final EnrollmentService enrollmentService;


    public ProfessorMenu(ConsoleInput input, ProfessorService professorService, EnrollmentService enrollmentService)
    {
        super(input);
        this.professorService = professorService;
        this.enrollmentService = enrollmentService;
    }

    @Override
    protected void showOptions()
    {
        System.out.println ("\n\n           ΔΙΑΧΕΙΡΙΣΗ ΚΑΘΗΓΗΤΩΝ");
        System.out.println ("=====================================\n");
        System.out.println ("[1]......ΕΙΣΑΓΩΓΗ\n");
        System.out.println ("[2]......ΕΥΡΕΣΗ ΜΕ ID\n");
        System.out.println ("[3]......ΕΜΦΑΝΙΣΗ ΟΛΩΝ\n");
        System.out.println ("[4]......ΕΠΕΞΕΡΓΑΣΙΑ\n");
        System.out.println ("[5]......ΔΙΑΓΡΑΦΗ\n");
        System.out.println ("[0]......ΕΠΙΣΤΡΟΦΗ\n");
        System.out.println ("=======================================");
    }

    @Override
    protected void handle(int choice) throws RegistryException
    {
        switch (choice)
        {
            case 1 -> addProfessor();
            case 2 -> findProfById();
            case 3 -> listProfessors();
            case 4 -> updateProfessor();
            case 5 -> deleteProfessor();
            default -> System.out.println("Μη έγκυρη επιλογή.");
        }
    }

    private void addProfessor() throws DuplicateEntityException
    {
        String profId = input.readString("Κωδικός καθηγητή: ");
        String specialty = input.readString("Ειδικότητα: ");
        String name = input.readString("Ονοματεπώνυμο: ");
        String email = input.readString("Email: ");
        String phone = input.readString("Τηλέφωνο: ");

        Professor professor = new Professor(profId, specialty, name, email, phone);
        professorService.addProfessor(professor);
        System.out.println("Ο καθηγητής προστέθηκε επιτυχώς.");
    }

    private void findProfById() throws EntityNotFoundException
    {
        String profId = input.readString("Εισάγετε τον κωδικό του καθηγητή προς αναζήτηση: ");
        Professor professor = professorService.getProfById(profId);
        System.out.println(professor);
    }

    private void listProfessors()
    {
        Collection<Professor> list = professorService.getAllProfessors();

        if (list.isEmpty())
        {
            System.out.println("Δεν υπάρχουν καθηγητές");
        }
        else
        {
            for (Professor professor : list)
            {
                System.out.println(professor);
            }
        }
    }

    private void updateProfessor() throws EntityNotFoundException
    {
        String profId = input.readString("Εισάγετε τον κωδικό του καθηγητή προς επεξεργασία: ");
        Professor professor = professorService.getProfById(profId);

        professor.setSpecialty(input.readStringOrKeep("Ειδικότητα ", professor.getSpecialty()));
        professor.setName(input.readStringOrKeep("Ονοματεπώνυμο ", professor.getName()));
        professor.setEmail(input.readStringOrKeep("Email ", professor.getEmail()));
        professor.setPhone(input.readStringOrKeep("Τηλέφωνο ", professor.getPhone()));

        professorService.updateProfessor(professor);
        System.out.println("Ο καθηγητής ενημερώθηκε επιτυχώς.");
    }

    private void deleteProfessor() throws EntityNotFoundException
    {
        String profId = input.readString("Εισάγετε τον κωδικό του καθηγητή προς διαγραφή: ");
        professorService.getProfById(profId);

        if (input.confirm("Διαγραφή του καθηγητή: " + profId + "; (ν/ο) "))
        {
            professorService.deleteProfessor(profId);
            System.out.println("Ο καθηγητής διαγράφηκε επιτυχώς.");
        }
        else
        {
            System.out.println("Η διαγραφή ακυρώθηκε.");
        }
    }
}
