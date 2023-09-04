package mx.unison;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomVendorFile {
    private String fileName;

    public RandomVendorFile(String fileName) {
        this.fileName = fileName;
    }

    public long write(Vendor v) {
        RandomAccessFile out = null;
        long position = 0;
        byte buffer[] = null;

        try {
            out = new RandomAccessFile(fileName, "rws");

            // cuantos bytes hay en archivo
            position = out.length();

            // ir al ultimo byte
            out.seek(position);

            // escribir el codigo
            out.writeInt(v.getCodigo());

            // escribir los bytes que se requieren para imprimir
            // la cadena con el nombre
            buffer = v.getNombre().getBytes();
            out.write(buffer);

            // convertir de Date a long
            long dob = v.getFecha().getTime();
            out.writeLong(dob);

            // escribir los bytes que se requieren para imprimir
            // la cadena con la zona
            buffer = v.getZona().getBytes();
            out.write(buffer);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return position;
    }


    public Vendor read(long position) {
        RandomAccessFile out = null;
        long bytes = 0;
        byte buffer[] = null;
        Vendor vendor = null;
        try {
            out = new RandomAccessFile(fileName, "rws");
            out.seek(position);

            int codigo = out.readInt();

            byte[] nameBytes = new byte[Vendor.MAX_NAME];
            out.read(nameBytes);

            long dateBytes = out.readLong();

            byte[] zonaBytes = new byte[Vendor.MAX_ZONE];
            out.read(zonaBytes);

            vendor = new Vendor(codigo, new String(nameBytes), new Date(dateBytes),
                    new String(zonaBytes));
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vendor;
    }

    public void read(Vendor vendors[]) {
        RandomAccessFile out = null;
        long bytes = 0;
        byte buffer[] = null;
        Vendor vendor = null;
        try {
            out = new RandomAccessFile(fileName, "rws");
            for (int i = 0; i < vendors.length; i++) {

                int codigo = out.readInt();

                byte[] nameBytes = new byte[Vendor.MAX_NAME];
                out.read(nameBytes);

                long dateBytes = out.readLong();

                byte[] zonaBytes = new byte[Vendor.MAX_ZONE];
                out.read(zonaBytes);

                vendors[i] = new Vendor(codigo, new String(nameBytes), new Date(dateBytes),
                        new String(zonaBytes));
            }
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public static void main(String[] args) {

        final String dataPath = "vendors-data.dat";

        RandomVendorFile randomFile = new RandomVendorFile(dataPath);

        Scanner input = new Scanner(System.in);
//Modificado por saul cardenas
        System.out.println("\tMenu\n");
        System.out.println("Que opcion quiere hacer?:\n\n" +
                "1.- Insertar vendedor\n" +
                "2.- Borrar datos de un vendedor\n" +
                "3.- Modificar datos de un vendedor(Excepto codigo de vendedor)\n" +
                "4.- Consulta por zona");
        int opcion = input.nextInt();
        input.nextLine();
        switch (opcion){
            case 1:
                System.out.println("Insertar codigo");
                int codigo = input.nextInt();
                input.nextLine();
                System.out.println("Insertar nombre:");
                String nombre = input.nextLine();
                System.out.println("Insertar Fecha Nacimiento (DD/MM/AA o DD/MM/YYYY): ");
                String fecha = input.nextLine();
                System.out.println("Insertar zona");
                String zona = input.nextLine();

                Vendor nuevoVendedor = new Vendor(codigo, nombre, fecha, zona);

                long posicion = randomFile.write(nuevoVendedor);
                break;


                //modificado por jose buzani
            case 2:
                                System.out.println("Insertar código del vendedor a borrar:");
                int codigoBorrar = input.nextInt();
                input.nextLine();

                try {
                    RandomAccessFile quitar = new RandomAccessFile(dataPath, "rw");
                    long currentPosition = 0;
                    boolean found = false;

                    while (quitar.getFilePointer() < quitar.length()) {
                        long currentPositionBeforeRead = quitar.getFilePointer();
                        int codigoConsulta = quitar.readInt();
                        byte[] nombreBytes = new byte[Vendor.MAX_NAME];
                        quitar.read(nombreBytes);
                        String nombreConsulta = new String(nombreBytes, "UTF-8").trim();
                        long fechaConsulta = quitar.readLong();
                        byte[] zonaBytes = new byte[Vendor.MAX_ZONE];
                        quitar.read(zonaBytes);
                        String zonaConsulta = new String(zonaBytes, "UTF-8").trim();

                        if (codigoConsulta == codigoBorrar) {

                            quitar.seek(currentPositionBeforeRead);
                            quitar.writeInt(-1);
                            quitar.write(new byte[Vendor.MAX_NAME]);
                            quitar.writeLong(0L);
                            quitar.write(new byte[Vendor.MAX_ZONE]);
                            found = true;
                            break;
                        }

                        currentPosition = quitar.getFilePointer();
                    }

                    if (found) {
                        System.out.println("Vendedor eliminado exitosamente.");
                    } else {
                        System.out.println("No se encontró ningún vendedor con ese código.");
                    }

                    quitar.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                break;
            case 3:
                break;
            case 4:
                System.out.println("Insertar zona a consultar:");
                String zonaConsultar = input.nextLine();

                try {
                    RandomAccessFile out2 = new RandomAccessFile(dataPath, "r");

                    while (out2.getFilePointer() < out2.length()) {
                        int codigoConsulta = out2.readInt();
                        byte[] nombreBytes = new byte[Vendor.MAX_NAME];
                        out2.read(nombreBytes);
                        String nombreConsulta = new String(nombreBytes, "UTF-8").trim();
                        long fechaConsulta = out2.readLong();
                        byte[] zonaBytes = new byte[Vendor.MAX_ZONE];
                        out2.read(zonaBytes);
                        String zonaConsulta = new String(zonaBytes, "UTF-8").trim();

                        if (zonaConsulta.equalsIgnoreCase(zonaConsultar)) {
                            Vendor vendedor = new Vendor(codigoConsulta, nombreConsulta, new Date(fechaConsulta), zonaConsulta);
                            System.out.println(vendedor);
                        }
                    }

                    out2.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;

            default:
                System.out.println("Opcion no valida");
        }


        input.close();
    }
}
