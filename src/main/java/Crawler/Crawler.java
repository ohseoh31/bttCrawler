package Crawler;

import Struct.*;
import Struct.BoardDTO;
import Struct.PostDTO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface Crawler {
    public void login(String id, String pw);
    public void setBoards(List<BoardDTO> boards);
    public boolean setBoard(BoardDTO board);

    public PostDTO getPost(BoardDTO board, int postNum) throws Exception;
    public List<PostDTO> getPosts(BoardDTO board, LocalDateTime start, LocalDateTime end);

    public List<PostDTO> getBoardsPosts(List<BoardDTO> boards, LocalDateTime start, LocalDateTime end);
    public int crawlTorrentSite();

    public List<TorrentFileDTO> saveTorrentFile(PostDTO post, String title);

    public List<PostImageDTO> savePostImage(PostDTO postDTO);


}
