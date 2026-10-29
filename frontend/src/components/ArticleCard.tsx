import type { LawArticle } from '../types/law';
import HighlightedText from './HighlightedText';

type ArticleCardProps = {
    article: LawArticle;
    expandedId: number | null;
    expandAll: boolean;
    setExpandedId: (id: number | null) => void;
    keyword: string;
};


function ArticleCard({
                         article,
                         expandedId,
                         expandAll,
                         setExpandedId,
                         keyword
                     }: ArticleCardProps) {
    return (
        <div className="article-card">
            <div className="article-meta">
                {article.lawName} · 제{article.articleNo}
                {article.articleBranchNo > 0
                    ? `조의${article.articleBranchNo}`
                    : '조'}
            </div>
            <h3><HighlightedText
                text={article.articleTitle}
                keyword={keyword}
                />
            </h3>
            <p className={
                (expandAll || article.articleId === expandedId)
                    ? "article-content expanded"
                    : "article-content preview"
            }>
                <HighlightedText
                    text={
                        (expandAll || article.articleId === expandedId)
                            ? article.articleContent
                            : article.articleContent.slice(0, 100)
                    }
                    keyword={keyword}
                />

                {!expandAll &&
                    article.articleId !== expandedId &&
                    article.articleContent.length > 100 &&
                    '...'}
            </p>
            {!expandAll && article.articleContent.length > 100 && (
                <button
                    className="more-button"
                    onClick={() => setExpandedId(
                        expandedId === article.articleId
                            ? null
                            : article.articleId
                    )}
                >
                    {expandedId === article.articleId ? '접기 ▲' : '더보기 ▼'}
                </button>
            )}
        </div>
    );
}

// 여기에 export 작성
export default ArticleCard;