import javax.swing.ImageIcon;

/**
 * @author Ijul
 */
public class Card
{
    private final int nilai; //variabel nilai kartu
    private final String jenis; // variabel jenis kartu
    private boolean mAtas; //variabel mengecek jika kartu menghadap atas


    public Card(int initnilai, String initjenis)
    {
        nilai = initnilai;
        jenis = initjenis;
        mAtas = false;
    }

    /**
     * getter untuk mengambil nilai dari kartu
     */
    public int getnilai()
    {
        return nilai;
    }
    
    /**
     * getter untuk mengambil jenis kartu 
     */
    public String getjenis()
    {
        return jenis;
    }
    
    /**
     * mengecek jika kartu bewarna merah (d)iamonds/wajik; (h)earts/hati
     */
    public boolean isRed()
    {
        return(jenis.equals("d") || jenis.equals("h"));
    }
    
    /**
     * mengecek apakah kartu menghadap atas
     */
    public boolean mAtas()
    {
        return mAtas;
    }
    
    /**
     * memutar kartu untuk terbuka ke atas
     */
    public void turnUp()
    {
        mAtas = true;
    }
    
    /**
     * mengubah kartu untuk menghadap kebawah
     */
    public void turnDown()
    {
        mAtas = false;
    }
    
    /**
     * mengambil nama dari file hartu serta menginisiasi file
     */
    public String getFileName()
    {
        //ubah firtpart menjadi directory file yang ada di lokal
        String firstPart = "C:\\Users\\dzulf\\Videos\\Tubes OOP\\src\\cards\\";
        String lastPart = ".gif";

        //khusus untuk back
        String filePath = "back";
        ImageIcon icon = new ImageIcon(filePath);

        if(!mAtas)
        {
            return firstPart + icon + lastPart;
        }
        else
        {
            String middlePart = getjenis();
            middlePart = switch (nilai) {
                case 1 -> "a" + middlePart;
                case 10 -> "t" + middlePart;
                case 11 -> "j" + middlePart;
                case 12 -> "q" + middlePart;
                case 13 -> "k" + middlePart;
                default -> getnilai() + middlePart;
            };
            return firstPart + middlePart + lastPart;
            //return kartu masuk ke dalam folder cards + nilai dari kartu + jenis file
        }
    }
} 
