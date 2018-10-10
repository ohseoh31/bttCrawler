package Crawler;

import Struct.BoardDTO;
import Struct.PostDTO;
import Struct.PostImageDTO;
import Struct.TorrentFileDTO;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BeeTorrentCrawl implements Crawler{
    private String SITE_URL = "https://torrentlin.com";
    private List<BoardDTO> boards = new ArrayList<>();
    private List<Selector> selectors = new ArrayList<>();

    public BeeTorrentCrawl(){
        selectors.add(new Selector(
                        "https://beetorrent.com/영화/최신영화/?board_name=newmovie",
                        "#newmovie_board_box > div.col-md-12.col-xs-12.basic-post-gallery > div:nth-child(1) > div > div.post-content.text-center > div.post-subject > a",
                        "/최신영화?board_name=newmovie&vid=", /*보류 : ../bbs/board.php?bo_table=torrent_movie_new&wr_id=*/
                        "https://beetorrent.com/영화/최신영화/?board_name=newmovie&vid=",
                        "#mb_newmovie_tr_title > td > span:nth-child(1)",
                        "",
                        "#mb_newmovie_tr_title > td > span:nth-child(2)",
                        "yyyy-MM-dd",
                        //TODO 토렌트 마그넷 정보는 내일하기
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(", 2, 3, ") > td > div > div:nth-child(1) > a:nth-child(3)",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(", 2, 3, ") > td > div > div:nth-child(1) > a:nth-child(2)"

//마그넷 : #mb_newmovie_tr_text > td > div > a
//토렌트 : #mb_newmovie_tr_file_download > td > div.icon_torrent > a
//이미지 : #mb_newmovie_tr_file_image > td > div > img
                )
        );
        selectors.add(new Selector(
                        "https://beetorrent.com/영화/지난영화/?board_name=oldmovie",
                        "#oldmovie_board_box > div.col-md-12.col-xs-12.basic-post-gallery > div:nth-child(1) > div > div.post-content.text-center > div.post-subject > a",
                        "/지난영화?board_name=oldmovie&vid=",
                        "https://beetorrent.com/영화/지난영화/?board_name=oldmovie&vid=",
                        "#mb_oldmovie_tr_title > td > span:nth-child(1)",
                        "",
                        "#mb_oldmovie_tr_title > td > span:nth-child(2)",
                        "yyyy-MM-dd HH:mm",
                        //보류
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(", 2, 3, ") > td > div > div:nth-child(1) > a:nth-child(3)",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(", 2, 3, ") > td > div > div:nth-child(1) > a:nth-child(2)"
                )
        );
    }


    @Override
    public void login(String id, String pw) {

    }


    @Override
    public boolean setBoard(BoardDTO board){
        for(Selector itrSelector: this.selectors){
            if(board.getBoardUrl().equals(itrSelector.BOARD_URL)){
                board.setSelector(itrSelector);
                continue;
            }
        }
        if(board.getSelector() == null) return false;
        try {
            Thread.sleep(1000);
            Document rawBoard =  Jsoup.connect(board.getBoardUrl())
                    .header("Origin", "http://beetorrent.com")
                    .header("Referer", "http://beetorrent.com")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Content-Type", "text/html;charset=iso-8859-1")
                    //.header("Accept-Encoding", "gzip, deflate, br")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .get();
            String LastUpdated = "-";
            String LastUpdatedList = rawBoard.select(board.getSelector().LAST_UPDATED_SELECTOR).attr("href");
            if(LastUpdatedList != null&& LastUpdatedList.length() != 0)
            {
                LastUpdated = rawBoard.select(board.getSelector().LAST_UPDATED_SELECTOR).attr("href").substring(board.getSelector().BOARD_BASE_URL.length());
            }
            String LastIndex = LastUpdated.replaceAll("[^0-9]", " ");
             board.getSelector().LAST_UPDATED_IDX = Integer.parseInt(LastIndex.trim().split(" ")[0]);
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    @Override
    public void setBoards(List<BoardDTO> boards) {
        this.boards= boards;
        for(BoardDTO itrBoard: this.boards){
            if(!setBoard(itrBoard)){
                System.out.println("no selector for "+ itrBoard.getBoardUrl());
            }
        }
    }

    private String selectFromDoc(Document doc, String selector){
        String result;
        try {
            result = doc.select(selector).get(0).text();
        }catch (IllegalArgumentException e){
            result = "-";
        } catch (IndexOutOfBoundsException e){
            result = "-";
        }
        return result;
    }

    @Override
    public PostDTO getPost(BoardDTO board, int postNum) throws Exception {
        Document rawPost = Jsoup.connect(board.getSelector().POST_BASE_URL+postNum)
                .header("Origin", "http://torrentlin.com")
                .header("Referer", board.getBoardUrl())
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Content-Type", "text/html;charset=iso-8859-1")
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .get();
        String title = selectFromDoc(rawPost,board.getSelector().TITLE_SELECTOR);
        String userName = selectFromDoc(rawPost,board.getSelector().USER_NAME_SELECTOR);
        String dateString=selectFromDoc(rawPost, board.getSelector().CREATED_DATE_SELECTOR);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(board.getSelector().DATE_TIME_FORMAT);
        LocalDateTime time = LocalDateTime.parse(dateString, formatter);
        PostDTO rstPost = new PostDTO(
                board.getSeq(),
                board.getSelector().POST_BASE_URL+postNum,
                title,userName,time,null,null
        );
/*
            List<PostImageDTO> postImageDTOS = savePostImage(rstPost);
        rstPost.setImages(postImageDTOS);

        List<TorrentFileDTO> torrentFileDTOs = saveTorrentFile(rstPost, title);
        rstPost.setTorrents(torrentFileDTOs);

 */

        return rstPost;
    }

    @Override
    public List<PostDTO> getPosts(BoardDTO board, LocalDateTime start, LocalDateTime end) {
        int num = board.getSelector().LAST_UPDATED_IDX;
        System.out.println(num);
        List<PostDTO> rstPosts = new ArrayList<>();
        while(true){
            try {
                PostDTO post = getPost(board,num);
                if(post.getPost_date().isBefore(start)) break;
                else if(post.getPost_date().isBefore(end)){
                    rstPosts.add(post);
                }
                else{
                    //TODO Remove 잔여 파일
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            num--;
        }
        return rstPosts;
    }

    @Override
    public List<PostDTO> getBoardsPosts(List<BoardDTO> boards, LocalDateTime start, LocalDateTime end) {
        List<PostDTO> result = new ArrayList<>();
        for (BoardDTO itrBoard:boards) {
            result.addAll(getPosts(itrBoard,start,end));
        }
        return result;
    }

    @Override
    public int crawlTorrentSite() {

        return 0;
    }

    @Override
    public List<TorrentFileDTO> saveTorrentFile(PostDTO post, String title) {
        return null;
    }

    @Override
    public List<PostImageDTO> savePostImage(PostDTO postDTO) {
        return null;
    }

    public List<PostDTO> testMethod(){
        List<PostDTO> posts = new ArrayList<>();
        try {
            posts = getPosts(boards.get(0),LocalDateTime.of(2018,10,3,12,1),LocalDateTime.of(2018,10,7,1,1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return posts;
    }


}
