import Crawler.Crawler;
import Crawler.TorrentLinCrawl;
import Crawler.*;
import Struct.BoardDTO;
import Struct.PostDTO;
import Struct.SiteDTO;
import bttSQL.DBHandler;

import java.util.ArrayList;
import java.util.List;

public class Controller {


    public static List<PostDTO> torrentLinCrawlStart(List<BoardDTO> tmpBoards, DBHandler bttDb){

        Crawler torrentLinCrawler = new TorrentLinCrawl();

        tmpBoards.add(bttDb.getBoardInfo("https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_new"));
        tmpBoards.add(bttDb.getBoardInfo("https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_old"));
        tmpBoards.add(bttDb.getBoardInfo("https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_3d"));
        torrentLinCrawler.setBoards(tmpBoards);

        List<PostDTO> posts = ((TorrentLinCrawl) torrentLinCrawler).testMethod();
        return posts;
    }

    public static List<PostDTO> daktoCrawlStart(List<BoardDTO> tmpBoards, DBHandler bttDb){

        Crawler daktoCrawl = new DaktoCrawl();

        tmpBoards.add(bttDb.getBoardInfo("https://dakto.org/d/new_movie"));
//        tmpBoards.add(bttDb.getBoardInfo("https://dakto.org/d/old_movie"));
//        tmpBoards.add(bttDb.getBoardInfo("https://dakto.org/d/kr_movie"));
//        tmpBoards.add(bttDb.getBoardInfo("https://dakto.org/d/us_movie"));
//        tmpBoards.add(bttDb.getBoardInfo("https://dakto.org/d/3d_movie"));
        daktoCrawl.setBoards(tmpBoards);

        List<PostDTO> posts = ((DaktoCrawl) daktoCrawl).testMethod();
        return posts;
    }

    public static List<PostDTO> beeCrawlStart(List<BoardDTO> tmpBoards, DBHandler bttDb){

        Crawler beeTorrentCrawl = new BeeTorrentCrawl();

        tmpBoards.add(bttDb.getBoardInfo("https://beetorrent.com/영화/최신영화/?board_name=newmovie"));
        tmpBoards.add(bttDb.getBoardInfo("https://beetorrent.com/영화/지난영화/?board_name=oldmovie"));
        tmpBoards.add(bttDb.getBoardInfo("https://beetorrent.com/영화/고화질영화/?board_name=hdmovie"));
        beeTorrentCrawl.setBoards(tmpBoards);

        List<PostDTO> posts = ((BeeTorrentCrawl) beeTorrentCrawl).testMethod();
        return posts;
    }


    public static void main(String[] args){

        //DB 접근
        DBHandler bttDb = new DBHandler();
        if(!bttDb.setCon()){
            System.out.println("DB connection fail\ncheck DBHandler.java");
            return;
        }

        Crawler torrentLinCrawler = new TorrentLinCrawl();
        Crawler beeTorrentCrawl = new BeeTorrentCrawl();

        SiteDTO torrentLinSite = bttDb.getSiteInfo("https://torrentlin.com");
        List<BoardDTO> tmpBoards = new ArrayList<>();

//        tmpBoards.add(bttDb.getBoardInfo("https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_new"));
//        tmpBoards.add(bttDb.getBoardInfo("https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_old"));
//        tmpBoards.add(bttDb.getBoardInfo("https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_3d"));
//        torrentLinCrawler.setBoards(tmpBoards);
//
//        List<PostDTO> posts = ((TorrentLinCrawl) torrentLinCrawler).testMethod();


//        tmpBoards.add(bttDb.getBoardInfo("https://beetorrent.com/영화/최신영화/?board_name=newmovie"));
//        tmpBoards.add(bttDb.getBoardInfo("https://beetorrent.com/영화/지난영화/?board_name=oldmovie"));
//        tmpBoards.add(bttDb.getBoardInfo("https://beetorrent.com/영화/고화질영화/?board_name=hdmovie"));
//        beeTorrentCrawl.setBoards(tmpBoards);


        /*
            TorrentLin 크롤러
         */
//        List<PostDTO> posts = torrentLinCrawler(tmpBoards, bttDb);

        /*
            Dakto 크롤러
         */
//        List<PostDTO> posts = daktoCrawlStart(tmpBoards, bttDb);

        /*
            BeeTorrent 크롤러
         */
        List<PostDTO> posts = beeCrawlStart(tmpBoards, bttDb);

        /*
            TorrentBoza
         */
//        List<PostDTO> posts = torrentbozaCrawlStart(tmpBoards, bttDb);


        bttDb.insertPosts(posts);
        bttDb.close();
    }
}

