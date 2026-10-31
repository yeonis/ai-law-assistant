export type LawArticle = {
    articleId: number;
    lawSerialNo: number;
    articleNo: number;
    articleBranchNo: number;
    articleTitle: string;
    articleContent: string;
    lawName: string | null;
};

export interface LawArticleCompare {
    oldArticle: LawArticle;
    newArticle: LawArticle;
}