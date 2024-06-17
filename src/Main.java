import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main{
    public static final String ARQUIVO_DESTINO = "./out/output.jpg";

    public static void main(String[] args) throws IOException {

        String arquivo_origem = args[1];

        BufferedImage ImagemOriginal = ImageIO.read(new File(arquivo_origem));
        BufferedImage ImagemResultado = new BufferedImage(ImagemOriginal.getWidth(), ImagemOriginal.getHeight(), BufferedImage.TYPE_INT_RGB);

        int width = ImagemOriginal.getWidth();
        int height = ImagemOriginal.getHeight();

        long startTime = System.currentTimeMillis();
        try {
            int numberOfThreads = Integer.parseInt(args[0]);
            if (numberOfThreads < 2) {
                recolorirUmaThread(ImagemOriginal, ImagemResultado);
            } else {
                recolorMultithreaded(ImagemOriginal, ImagemResultado, numberOfThreads);
            }
        } catch (Exception e) {
            System.out.println("Single thread");
            recolorirUmaThread(ImagemOriginal, ImagemResultado);
        }

        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;

        File outputFile = new File(ARQUIVO_DESTINO);
        ImageIO.write(ImagemResultado, "jpg", outputFile);

        System.out.println(String.valueOf(duration));


    }

    public static void recolorFracionado(BufferedImage ImagemOriginal, BufferedImage ImagemResultado, int partes) {
        int width = ImagemOriginal.getWidth();
        int height = ImagemOriginal.getHeight()/partes;

        for(int i = 0; i < partes; i++) {
            final int multiplicadorInicio = i;
            int xInicio = 0;
            int yInicio = height*multiplicadorInicio;

            recolorirImagem(ImagemOriginal, ImagemResultado, xInicio, yInicio, width, height);
        }

    }

    public static void recolorMultithreaded(BufferedImage ImagemOriginal, BufferedImage ImagemResultado, int numberOfThreads) {
        List<Thread> threads = new ArrayList<>();
        int width = ImagemOriginal.getWidth();
        int height = ImagemOriginal.getHeight() / numberOfThreads;

        for(int i = 0; i < numberOfThreads ; i++) {
            final int threadMultiplier = i;

            Thread thread = new Thread(() -> {
                int xOrigin = 0 ;
                int yOrigin = height * threadMultiplier;

                recolorirImagem(ImagemOriginal, ImagemResultado, xOrigin, yOrigin, width, height);
            });

            threads.add(thread);
        }

        for(Thread thread : threads) {
            thread.start();
        }

        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
    }

    public static void recolorirUmaThread(BufferedImage ImagemOriginal, BufferedImage ImagemResultado) {
        recolorirImagem(ImagemOriginal, ImagemResultado, 0, 0, ImagemOriginal.getWidth(), ImagemOriginal.getHeight());
    }

    public static void recolorirImagem(BufferedImage ImagemOriginal, BufferedImage ImagemResultado, int leftCorner, int topCorner,
                                       int width, int height) {
        for(int x = leftCorner ; x < leftCorner + width && x < ImagemOriginal.getWidth() ; x++) {
            for(int y = topCorner ; y < topCorner + height && y < ImagemOriginal.getHeight() ; y++) {
                recolorirPixel(ImagemOriginal, ImagemResultado, x , y);
            }
        }
    }

    public static void recolorirPixel(BufferedImage ImagemOriginal, BufferedImage ImagemResultado, int x, int y) {
        int rgb = ImagemOriginal.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;
        //aqui vamos popular os novos pixels
        //se o pixel em quest o for um tom de cinza, vamos aumentar o n vel de vermelho em 10; o de verde diminuir 80, azul dimiuir 20
        if(ehNivelDeCinza(red, green, blue)) {

            newRed = Math.max(0, red - 60);
            newGreen = Math.max(0, green - 10);
            newBlue = Math.min(255, blue + 30);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        //M todo para setar valor rgb na coordenada do pixel da imagem
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(ImagemResultado, x, y, newRGB);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }
    //metodo para verificar se o pixel   tom de cinza (estar  na parte branca da flor)
    //Checa se todos os componentes tem uma intensidade similar (< 30 - determinado empiricamente)
    public static boolean ehNivelDeCinza(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs( green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        //opera  o de OR deslocando para esquerda em cada cor
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}



