import java.util.*;
/**
 * @author Ijul
 */
public class Solitaire{    
    public static void main(String[] args) {
        new Solitaire();
    }

        private final Stack<Card> stock;
        private final Stack<Card> waste;
        private final Stack<Card>[] foundations; 
        private final Stack<Card>[] piles; 
        private final SolitaireDisplay display; 

    /**
     * Construktor
     */
    @SuppressWarnings("unchecked")
    public Solitaire()
    {
        stock = new Stack<>();
        waste = new Stack<>();
        foundations = new Stack[4];
        for(int i = 0; i<4; i++)
        {
            foundations[i] = new Stack<>();
        }
        piles = new Stack[7];
        for(int i = 0; i<7; i++)
        {
            piles[i] = new Stack<>();
        }
        display = new SolitaireDisplay(this);
        createStock();
        deal();
    }

    /**
     * mengecek kartu dari tumpukan kartu, jika tidak ada sisa kartu maka akan null
     */
    public Card getStockCard()
    {
        if(stock.isEmpty())
        {
            return null;
        }
        else
        {
            return stock.peek();
        }
    }

    /**
     * mengecek kartu dari tumpukan kartu yang sudah dibuka, jika kosong maka null
     */
    public Card getWasteCard()
    {
        if(waste.isEmpty())
        {
            return null;
        }
        else
        {
            return waste.peek();
        }
    }

    /**
     * mengecek kartu pondasi yang ada di kolom pondasi kartu 
     */
    public Card getFoundationCard(int index)
    {
        Stack<Card> temp = foundations[index];
        if(temp.isEmpty())
        {
            return null;
        }
        else
        {
            return temp.peek();
        }
    }

    /**
     * mengecek kartu sesuai dengan dia di bagian tableau berapa dan nilainya
     */
    public Stack<Card> getPile(int index)
    {
        return piles[index];
    }
    
    /**
     * membuat tumpukan kartu jenis (52 total kartu, masing" indeks (d)iamonds, (h)earts, (s)pades, (c)lubs, serta nilainya(jeniss) ace,2,3,4,5,6,7,8,9,j,q,k
     * diambil dari kelas cards)
     */
    private void createStock()
    {
        ArrayList<Card> temp = new ArrayList<Card>(52);
        String jenis;
        for(int nilaiIndex = 1; nilaiIndex<=13; nilaiIndex++)
        {
            for(int jenisIndex = 1; jenisIndex<=4; jenisIndex++)
            {
                jenis = switch (jenisIndex) {
                    case 1 -> "c";
                    case 2 -> "d";
                    case 3 -> "h";
                    default -> "s";
                };
                temp.add(new Card(nilaiIndex, jenis));
            }
        }
        
    /**
     * inisiasi jumlah dari banyak kartu (52) lalu meng-shuffle dari kartu" yang ada
     */
        int high = 52;
        while(!temp.isEmpty())
        {
            stock.add(temp.remove((int)(Math.random() * high)));
            high--;
        }
    }
    
    /**
     * membuat barisan kartu untuk dimainkan ke dalam layar
     */
    private void deal()
    {
        for(int i = 0; i<7; i++)
        {
            for(int j = 0; j<=i; j++)
            {
                if(!stock.isEmpty())
                {
                    Card c = stock.remove(0);
                    if(j==i)
                    {
                        c.turnUp();
                    }
                    piles[i].add(c);
                }
            }
        }
    }

    /**
     * logika pengambilan kartu dari tumpukan kartu, diambil 1 per 1
     */
    private void dealOneCard()
    {
        if (!stock.isEmpty()) {
            Card c = stock.pop();
            c.turnUp();
            waste.push(c);
        }
    }

    
    /**
     * membalikkan posisi dari tumpukan kartu yang sudah dibuka
     */
    private void resetStock()
    {
        while(!waste.isEmpty())
        {
            Card c = waste.pop();
            c.turnDown();
            stock.push(c);
        }
    }
    
    /**
     * interaksi player dengan tumpukan kartu, akan mengambil 1 atau akan membalikkan tumpukan kartu
     */
    @SuppressWarnings("UnnecessaryReturnStatement")
    public void stockClicked()
    {
        if(display.isWasteSelected() || display.isPileSelected())
        {
            return;
        }
        else if(!stock.isEmpty())
        {
            dealOneCard();
        }
        else
        {
            resetStock();
        }
    }

    /**
     * toggle untuk pemilihan kartu, di klik ulang untuk unselect kartu
     */
    public void wasteClicked()
    {
        if(!waste.isEmpty() && !display.isWasteSelected() && !display.isPileSelected())
        {
            display.selectWaste();
        }
        else if(display.isWasteSelected())
        {
            display.unselect();
        }
    }

    /**
     * klik pada pondasi, memindahkan kartu dari tumpukan/table yang akan dipilih untuk masuk ke pondasi jika memenuhi syarat
     */
    public void foundationClicked(int index)
    {
        if(display.isWasteSelected() && canAddToFoundation(waste.peek(), index))
        {
            foundations[index].push(waste.pop());
            display.unselect();
        }
        else if(display.isPileSelected()
                && canAddToFoundation(piles[display.selectedPile()].peek(), index))
        {
            foundations[index].push(piles[display.selectedPile()].pop());
            display.unselect();
        }
        
    }
    

    /**
     * menangani klik pada tabel, memindahkan tumpukan tabel ke tempat lain, memindahkan kartu dari tumpukan ke tabel, memutar kartu yang terbalik
     */
    public void pileClicked(int index)
    {
        if(display.isPileSelected() && index!=display.selectedPile())
        {
            Stack<Card> s = removeFaceUpCards(display.selectedPile());
            if(canAddToPile(s.peek(), index))
            {
                addToPile(s, index);
                display.unselect();
            }
            else
            {
                addToPile(s, display.selectedPile());
            }
        }
        else if(display.isWasteSelected())
        {
            Card c = waste.peek();
            if(canAddToPile(c, index))
            {
                piles[index].push(waste.pop());
                display.unselect();
            }
        }
        else if(!display.isWasteSelected() && !display.isPileSelected() && 
                !piles[index].isEmpty() && piles[index].peek().mAtas())
        {   
            display.selectPile(index);
        }    
        else if(display.selectedPile()==index)
        {
            display.unselect();
        }
        else if(!display.isWasteSelected()
                && !display.isPileSelected() && !piles[index].isEmpty() 
                && !piles[index].peek().mAtas())
        {
            piles[index].peek().turnUp();
        }
    }
    

        /**
     * mengecek apakah pondasi sudah lengkap dengan semua indeks dan kartu secara runtut
     */
    public boolean celebrateTime()
    {
        for(int i = 0; i<4; i++)
        {
            Stack<Card> temp = foundations[i];
            if(temp.isEmpty())
            {
                return false;
            }
            else if(temp.peek().getnilai()!=13)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * mengecek apakah kartu bisa masuk ke dalam tabel, dengan logika dari besar ke kecil, dan warna yang harus selang-seling
     */
    private boolean canAddToPile(Card card, int index)
    {
        if(piles[index].isEmpty())
        {
            return card.getnilai()==13;
        }
        
        Card pileCard = piles[index].peek();
        if(pileCard.mAtas())
        {
            String cardjenis = card.getjenis();
            String pileCardjenis = pileCard.getjenis();
            if(card.getnilai()+1==pileCard.getnilai() && 
                ((cardjenis.equals("h") || cardjenis.equals("d")) &&
                (pileCardjenis.equals("c") || pileCardjenis.equals("s")) ||
                (cardjenis.equals("c") || cardjenis.equals("s")) &&
                (pileCardjenis.equals("h") || pileCardjenis.equals("d"))))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * untuk select satu tumpukan yang sudah menumpuk dan terbuka secara bersamaan
     */
    private Stack<Card> removeFaceUpCards(int index)
    {
        Stack<Card> s = new Stack<>();
        while(!piles[index].isEmpty() && piles[index].peek().mAtas())
        {
            s.push(piles[index].pop());
        }
        return s;
    }
    
    /**
     * untuk memindahkan multi-kartu antar tumpukan
     */
    private void addToPile(Stack<Card> cards, int index)
    {
        while(!cards.isEmpty())
        {
            piles[index].push(cards.pop());
        }
    }
    
    /**
     * mengecek apakah kartu boleh masuk ke dalam pondasi sort, mencakup nilai nilai yang ascending dan indeks kartu yang sama
     */
    private boolean canAddToFoundation(Card card, int index)
    {
        if(foundations[index].isEmpty())
        {
            return card.getnilai()==1;
        }
        else
        {
            return (getFoundationCard(index).getnilai()==card.getnilai()-1 
                    && getFoundationCard(index).getjenis().equals(card.getjenis()));
        }
    }


}


