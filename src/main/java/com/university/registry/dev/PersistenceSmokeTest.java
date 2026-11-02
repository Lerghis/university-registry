package com.university.registry.dev;

import com.university.registry.model.Student;
import com.university.registry.repository.StudentRepository;
import jdk.swing.interop.SwingInterOpUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PersistenceSmokeTest implements CommandLineRunner
{
    private final StudentRepository studentRepository;

    public PersistenceSmokeTest(StudentRepository studentRepository)
    {
        this.studentRepository = studentRepository;
    }


    @Override
    public void run(String... args) throws Exception
    {
        Student student = new Student("AM9001", 1, "Spring test", "t@x.gr", "100");

        studentRepository.save(student);
        System.out.println("Stored: " + studentRepository.findById("AM9001").get());
        System.out.println("Students in the table: " + studentRepository.findAll().size());

        studentRepository.deleteById("AM9001");
        System.out.println("Still exists after delete? " + studentRepository.existsById("AM9001"));
    }
}
