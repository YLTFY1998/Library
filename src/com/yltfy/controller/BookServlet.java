package com.yltfy.controller;

import com.yltfy.entity.Book;
import com.yltfy.entity.Borrow;
import com.yltfy.entity.Reader;
import com.yltfy.service.BookService;
import com.yltfy.service.impl.BookServiceImpl;
import jdk.jshell.spi.SPIResolutionException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 图书展示等操作的控制器
 */
@WebServlet("/book")
public class BookServlet extends HttpServlet {
    private BookService bookService = new BookServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method == null)
            method = "findAll";
        HttpSession session = req.getSession();
        Reader reader = (Reader) session.getAttribute("reader");
        switch (method) {
            case "findAll":
                //获取数据
                String pageStr = req.getParameter("page");
                Integer page = Integer.parseInt(pageStr);
                //获取当前分页的数据 以及总页数
                List<Book> list = bookService.findAll(page);
                req.setAttribute("pages", bookService.getPages());
                req.setAttribute("list", list);
                req.setAttribute("dataPrePage", 6);
                req.setAttribute("currentPage", page);
                //将数据发送到视图
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
                break;
            case "addBorrow":
                String bookidStr = req.getParameter("bookid");
                Integer bookid = Integer.parseInt(bookidStr);
                //向Service发送借书请求
                //前一层给后一层传数据（调用后一层的方法）都是用了后一层的接口的多态去调用
                bookService.addBorrow(bookid, reader.getId());
                //跳转到当前用户所借的书的汇总界面
                resp.sendRedirect("/book?method=findAllBorrow&page=1");
                break;
            case "findAllBorrow":
                pageStr = req.getParameter("page");
                page = Integer.parseInt(pageStr);
                List<Borrow> borrowList =  bookService.findAllBorrowByReaderId(reader.getId(), page);
                req.setAttribute("borrowList", borrowList);
                req.setAttribute("dataPrePage", 6);
                req.setAttribute("currentPage", page);
                req.setAttribute("pages", bookService.getBorrowPages(reader.getId()));
                req.getRequestDispatcher("/borrow.jsp").forward(req, resp);
                break;
        }
    }
}
