package Crawler;

import Struct.BoardDTO;
import Struct.PostDTO;
import Struct.PostImageDTO;
import Struct.TorrentFileDTO;
import javafx.geometry.Pos;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TorrentLinCrawl implements Crawler {

    private String SITE_URL = "https://torrentlin.com";
    private List<BoardDTO> boards = new ArrayList<>();
    private List<Selector> selectors = new ArrayList<>();

    public TorrentLinCrawl(){
        selectors.add(new Selector(
                        "https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_new",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(4) > tbody > tr > td > form > table > tbody > tr:nth-child(2) > td.subject > nobr > a",
                        //body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(4) > tbody > tr > td > form > table > tbody > tr:nth-child(2) > td.subject > nobr > a
                        "../bbs/board.php?bo_table=torrent_movie_new&wr_id=",
                        "https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_new&wr_id=",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(1) > tbody > tr > td:nth-child(1) > h1",
                        "",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(1) > div:nth-child(1) > span",
                        "업데이트 : yy-MM-dd HH:mm",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(", 2, 3, ") > td > div > div:nth-child(1) > a:nth-child(3)",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(", 2, 3, ") > td > div > div:nth-child(1) > a:nth-child(2)"
                )
        );
        selectors.add(new Selector(
                        "https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_old",
                        //body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(4) > tbody > tr > td > form > table > tbody > tr:nth-child(2) > td.subject
                        "td.subject a[href]",
                        "../bbs/board.php?bo_table=torrent_movie_old&wr_id=",
                        "https://torrentlin.com/bbs/board.php?bo_table=torrent_movie_old&wr_id=",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(1) > tbody > tr > td:nth-child(1) > h1",
                        "",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(1) > div:nth-child(1) > span",
                        "업데이트 : yy-MM-dd HH:mm",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(", 2, 3, ") > td > div > div:nth-child(1) > a:nth-child(3)",
                        "body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(3) > tbody > tr:nth-child(", 2, 3, ") > td > div > div:nth-child(1) > a:nth-child(2)"
                )
        );
    }

    @Override
    public void login(String id, String pw) {
        //TODO
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
                    .header("Origin", "http://torrentlin.com")
                    .header("Referer", "http://torrentlin.com")
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

        List<PostImageDTO> postImageDTOS = savePostImage(rstPost);
        rstPost.setImages(postImageDTOS);

        List<TorrentFileDTO> torrentFileDTOs = saveTorrentFile(rstPost, title);
        rstPost.setTorrents(torrentFileDTOs);

        //TODO 이미지 추가하는 부분이 필요

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
        List<TorrentFileDTO> torrentFiles = new ArrayList<>();
        try {
            Connection.Response loginPageResponse = Jsoup.connect(post.getPost_link())
                    .timeout(3000)
                    .header("Origin", "http://torrentlin.com")
                    .header("Referer", "http://torrentlin.com")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .method(Connection.Method.GET)
                    .execute();
            Map<String, String> cookie = loginPageResponse.cookies();
            Document tmpDocument = loginPageResponse.parse();

            Elements elems2 = tmpDocument.select("body > div > table > tbody > tr:nth-child(6) > td > table > tbody > tr > td:nth-child(1) > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div:nth-child(2) > table:nth-child(3) > tbody > tr > td > div > div a[href]");

            TorrentFileDTO tmpTorrent = new TorrentFileDTO();
            tmpTorrent.setPost_seq(post.getSeq());

            for (Element elem2 : elems2) {
                if(elem2.attr("href").contains("torrent") && ( !elem2.attr("href").contains("srt") && !elem2.attr("href").contains("smi"))) {
                    //System.out.println(elem2.attr("href"));
                    String downLoadScript = elem2.attr("href");
                    String[] downloadInfo = downLoadScript.split(",");
                    //TODO replace 한줄로 바꾸기
                    downloadInfo[0]= downloadInfo[0].replace("javascript:file_download('..","https://torrentlin.com").replace("'","");
                    downloadInfo[1]= downloadInfo[1].replace("\'","").replace(");","")
                            .replace("\\","")
                            .replace("/","")
                            .replace(":","")
                            .replace("*","")
                            .replace("?","")
                            .replace("\"","")
                            .replace("<","")
                            .replace(">","")
                            .replace("|","")
                            .replace("\'","");
                        //TODO ' replace 구문 만들기
                        //TODO replace 합치기
//                    System.out.println("link : "+ downloadInfo[0]);
//                    System.out.println("file_ tilte : "+ downloadInfo[1]);
                    Connection.Response response = Jsoup.connect(downloadInfo[0])
                            .header("Origin", "http://torrentlin.com")
                            .header("Referer", post.getPost_link())
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                            .header("Content-Type", "text/html;charset=iso-8859-1")
                            .header("Accept-Encoding", "gzip, deflate, br")
//                                    .header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4")
                            .method(Connection.Method.GET)
                            .cookies(cookie)
                            .ignoreContentType(true)
                            .execute();

                    String torrentFilePath = URLDecoder.decode(downloadInfo[1],"UTF-8");
                    System.out.println("토렌트 파일 명 : "+torrentFilePath);

                    FileOutputStream out = new FileOutputStream(new java.io.File(torrentFilePath));
                    out.write(response.bodyAsBytes());
                    out.close();
                    tmpTorrent.setInfohash_dir(torrentFilePath);
                }

                if(elem2.attr("href").contains("magnet")){
                    System.out.println("마그넷 주소 : "+elem2.attr("href"));
                    tmpTorrent.setMagnet(elem2.attr("href"));
                    torrentFiles.add(tmpTorrent);
                }

            }
        } catch (Exception e) {
            return null;
        }
//        post.setTorrents(torrentFiles);
        return torrentFiles;
    }

    @Override
    public List<PostImageDTO> savePostImage(PostDTO postDTO) {

        List<PostImageDTO> postImageDTOS = new ArrayList<>();


        try {
            Connection.Response imgPathResponse = Jsoup.connect(postDTO.getPost_link())
                    .timeout(3000)
                    .header("Origin", "http://torrentlin.com")
                    .header("Referer", "http://torrentlin.com")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .method(Connection.Method.GET)
                    .execute();

            Document tmpDocument = imgPathResponse.parse();
            Elements elems = tmpDocument.select("#writeContents > article > img");
            for (Element elem : elems){





                Pattern p = Pattern.compile("\\.\\.[/_0-9a-zA-Z]*\\.(jpg|png|gif)");
                Matcher m = p.matcher(elem.toString());

                if(m.find()){
                    if (m.group(0) !=null){
                        Connection.Response response = Jsoup.connect( this.SITE_URL + m.group(0).replace("..",""))
                                .header("Origin", "http://torrentlin.com")
                                .header("Referer", postDTO.getPost_link())
                                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                                .header("Content-Type", "text/html;charset=iso-8859-1")
                                .header("Accept-Encoding", "gzip, deflate, br")
                                .method(Connection.Method.GET)
                                .ignoreContentType(true)
                                .execute();

                        FileOutputStream out = new FileOutputStream(new java.io.File(m.group(0).split("/")[5]));
                        out.write(response.bodyAsBytes());
                        out.close();
                    }
                }

                PostImageDTO postImageDTO = new PostImageDTO(m.group(0).split("/")[5],"");
                postImageDTO.setPost_seq(postDTO.getSeq());

                postImageDTOS.add(postImageDTO);
            }



//            tmpTorrent.setPost_seq(post.getSeq());
        } catch (Exception e) {
            return null;
        }
        return postImageDTOS;
    }


    //getPost 함수의 첫번째 인자는 post table의 마지막 저장 시간
    //두번째 인자는 현재 시간을 저장하는 방식을 사용
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
